package com.liukai.eshop.storm;

import com.liukai.eshop.common.util.HttpUtils;
import com.liukai.eshop.common.util.zookeeper.ZooKeeperSession;
import com.liukai.eshop.common.util.zookeeper.ZookeeperConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.streams.Pair;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 热数据——商品id 统计与上报 bolt
 */
@Slf4j
public class ProductCountBolt extends BaseRichBolt {

  public static final int TOP_N = 3;

  /**
   * 分发层 NGINX URL
   */
  public static final String DISTRIBUTE_NGINX_URL = "http://www.kaisesai.com:80/hot?productId=%s";

  /**
   * 缓存服务获取商品信息 URL
   */
  public static final String CACHE_SERVICE_GET_PRODUCT_INFO_URL
    = "http://127.0.0.1:8090/cache/getProductInfo?product_id=%s";

  /**
   * 应用层 NGINX URL
   */
  public static final String APP_NGINX_URL
    = "http://www.kaisesai.com:81/hot?productId=%s&productInfo=%s";

  /**
   * 分发层取消热点商品缓存 URL
   */
  public static final String CANCEL_HOT_URL = "http://www.kaisesai.com:81/cancel_hot?productId=%s";

  /**
   * LRU 缓存，其中 key 为商品 id，value 为出现的次数
   */
  private final LRUMap<Long, Long> countMap = new LRUMap<>(100);

  private ZooKeeperSession zooKeeperSession;

  private String taskId = "-1";

  private String taskNodePath;

  public static void main(String[] args) {

    HotPointProductFindTask hotPointProductFindTask = new HotPointProductFindTask(null);
    hotPointProductFindTask.pushHotProductIdToNginx(1L);

    // LRUMap<Long, Long> productCountLruMap = new LRUMap<>(100);
    // IntStream.range(0, 100).forEach(value -> {
    //   productCountLruMap.put((long) value, (long) RandomUtils.nextInt());
    // });
    // HotPointProductFindTask hotPointProductFindTask = new HotPointProductFindTask(
    //   productCountLruMap);
    // Thread t1 = new Thread(hotPointProductFindTask);
    // t1.start();
    //
    // // 睡 10 秒
    // Utils.sleep(10 * 1000);
    // t1.interrupt();

  }

  @Override
  public void prepare(Map<String, Object> topoConf, TopologyContext context,
                      OutputCollector collector) {

    /*
      本次统计热点数据的 zk 路径有：
        1. 冷启动任务路径："/hot-product-task-{taskId}"，该路径下存储的数据为，
          该 taskid 统计的热点商品的 topn 的 productId 数据，用逗号分隔；

        2. 冷启动任务锁："/hot-product-task-list-lock"，该路径为预热操作的分布式锁，因为预热的程序会有多个线程去执行，
          每个线程执行时需要获取该锁

        3. 冷启动热点数据任务节点路径："/hot-product-task-list"，该路径下存储的数据是，每个任务自己的 taskid，用逗号分割。
     */

    // 初始化分布式锁
    zooKeeperSession = ZooKeeperSession.getInstance();
    // 初始化任务 id
    taskId = String.valueOf(context.getThisTaskId());

    taskNodePath = ZookeeperConstant.STORM_HOT_PRODUCT_TASK_PATH + taskId;

    // 启动热数据与热点数据上报线程任务
    startTopNThread();

    // 热点数据结点路径写入 zookeeper
    // 上报自己的 taskid 到列表
    writeTaskPathToZK();
  }

  /**
   * 将任务 id 数据写入任务路径上
   */
  private void writeTaskPathToZK() {
    // 获取分布式锁
    zooKeeperSession.acquireDistributedLock(ZookeeperConstant.STORM_HOT_PRODUCT_TASK_LIST_LOCK, "");
    // 获取任务节点路径的数据
    String nodeData = zooKeeperSession.getNodeData(ZookeeperConstant.STORM_TASK_LIST_NODE);
    // 如果已经存在，检查自己的 taskid，把自己的 taskid 追加到尾部
    if (StringUtils.isNotBlank(nodeData)) {
      if (!Arrays.asList(StringUtils.split(nodeData, ",")).contains(taskId)) {
        nodeData += ("," + taskId);
      }
    } else {
      nodeData = taskId;
    }
    // 写入任务节点数据
    zooKeeperSession.setNodeData(ZookeeperConstant.STORM_TASK_LIST_NODE, nodeData);
    // 释放锁
    zooKeeperSession.releaseDistributedLock(ZookeeperConstant.STORM_HOT_PRODUCT_TASK_LIST_LOCK, "");
  }

  /**
   * 启动热数据 TopN 统计线程
   */
  private void startTopNThread() {
    // 热商品统计与上报线程任务
    new Thread(new HotProductTopNTask(countMap)).start();
    // 热点商品统计与上报线程任务
    new Thread(new HotPointProductFindTask(countMap)).start();
  }

  @Override
  public void execute(Tuple input) {
    Long productId = input.getLongByField("productId");
    countMap.compute(productId, (k, v) -> {
      if (v == null) {
        return 1L;
      } else {
        return v + 1;
      }
    });
    log.info("商品 {}, 次数 {}", productId, countMap.get(productId));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {

  }

  /**
   * 商品热点数据统计与上报任务
   * <p>
   * 热数据与热点数据不是回事。
   * <p>
   * 1. 热点数据是热数据中，访问量很高的数据。
   * <p>
   * 2. 通过对热数据按照访问量进行排序，计算出后 95% 的数据平均值。
   * <p>
   * 3. 对前 5% 的数据进行热点阈值评估，大于 n 倍的视为热点商品，存储在热点商品列表中。
   */
  private static class HotPointProductFindTask implements Runnable {

    private final LRUMap<Long, Long> productCountLruMap;

    public HotPointProductFindTask(LRUMap<Long, Long> productCountLruMap) {
      this.productCountLruMap = productCountLruMap;
    }

    @Override
    public void run() {
      List<Map.Entry<Long, Long>> countList = new ArrayList<>();
      // 本次推送的热点数据
      List<Long> hotPidList = new ArrayList<>();
      // 上一次推送的热点数据
      List<Long> lastTimeHotPidList = new ArrayList<>();

      // 5 秒钟统计一次
      while (!Thread.currentThread().isInterrupted()) {
        // Utils.sleep(5 * 1000);
        if (productCountLruMap.size() < 2) {
          // 至少有两个商品
          continue;
        }
        // 1. 统计 LRUMap 数据，按照访问量进行排序
        countList.clear();
        hotPidList.clear();

        countList.addAll(productCountLruMap.entrySet());

        // 按照访问量降序排序
        countList.sort((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()));

        // 2. 统计出后 95% 平均值
        int avg95Count = (int) (countList.size() * 0.95);
        int avg95Total = 0;
        for (int i = countList.size() - 1; i >= (countList.size() - avg95Count); i--) {
          avg95Total += countList.get(i).getValue();
        }

        // 后 95% 的平均值
        int avg95Avg = avg95Total / avg95Count;
        // 阈值
        int threshold = 5;
        int campareValue = threshold * avg95Avg;

        // 3. 对前 5% 的数据进行热点阈值评估
        countList.stream().limit(5).forEach(entry -> {
          if (entry.getValue() > campareValue) {
            hotPidList.add(entry.getKey());
            if (!lastTimeHotPidList.contains(entry.getKey())) {
              // 发送 http 请求到 nginx，进行热数据缓存生成
              // 如果该商品已经是热点商品了，则不推送，新热点商品才推送
              pushHotProductIdToNginx(entry.getKey());
            }
          }
        });
        log.info("热点商品列表：" + hotPidList);

        // 4. 热点数据消息，通知 nginx 取消热点缓存
        for (Long lastTimeHotPid : lastTimeHotPidList) {
          // 但是不在这次的热点中，说明热点消失了
          if (!hotPidList.contains(lastTimeHotPid)) {
            // 发送到分发层
            String cancelHotUrl = String.format(CANCEL_HOT_URL, lastTimeHotPid);
            HttpUtils.get(cancelHotUrl);
          }
        }

        lastTimeHotPidList.clear();
        lastTimeHotPidList.addAll(hotPidList);

        // 休眠 5 秒钟
        try {
          Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    private void pushHotProductIdToNginx(Long productId) {
      // 商品降级策略
      // 推送到分发层 nginx ，这里我的本地分发层 nginx 有点问题
      // String distributeNginxUrl = String.format(DISTRIBUTE_NGINX_URL, productId);
      // HttpUtils.get(distributeNginxUrl);

      // 直接调用后端服务，返回商品数据
      // String cacheServiceGetProductInfoUrl = String
      //   .format(CACHE_SERVICE_GET_PRODUCT_INFO_URL, productId);

      // String response = HttpUtils.get(cacheServiceGetProductInfoUrl);
      // 直接写死数据
      String response
        = "{\"status\":0,\"desc\":\"成功\",\"data\":{\"id\":1,\"create_time\":null,\"update_time\":null,\"name\":\"iphone7手机\",\"price\":5599,\"picture_list\":\"a.jpg,b.jpg\",\"specification\":\"iphone7的规格\",\"service\":\"iphone7的售后服务\",\"color\":\"红色,白色,黑色\",\"size\":\"5.5\",\"shop_id\":1}}";

      // 商品信息
      String productInfoData;
      try {
        productInfoData = URLEncoder.encode(response, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
        log.error("URLEncoder fail, response:{}", response);
        return;
      }

      // 推送到应用层 nginx
      String appNginxUrl = String.format(APP_NGINX_URL, productId, productInfoData);
      HttpUtils.get(appNginxUrl);
    }
  }

  /**
   * 商品热数据 TopN 统计与上报任务
   */
  private class HotProductTopNTask implements Runnable {

    private final LRUMap<Long, Long> lruMap;

    public HotProductTopNTask(LRUMap<Long, Long> lruMap) {
      this.lruMap = lruMap;
    }

    @Override
    public void run() {
      // 优先级队列（小顶堆），用于查询 topn，Pair<Long, Long> left 为商品 id，right 为出现的次数
      PriorityQueue<Pair<Long, Long>> priorityQueue = new PriorityQueue<>(TOP_N, Comparator
        .comparingLong(Pair::getSecond));

      while (true) {
        // 每分钟检查一次，堆中的数据
        lruMap.forEach((k, v) -> {
          // 不管有没有满，都需要
          boolean ifDeleted = updateQueueData(priorityQueue, k, v);

          // 如果没更新，说明小顶堆中就没有这样的数据，则进行与堆顶元素进行比较，并根据比较结果更新堆数据
          if (!ifDeleted) {
            if (priorityQueue.size() < TOP_N) {
              // 直接将 lrumap 中的数据放入堆中
              priorityQueue.add(Pair.of(k, v));
            } else {
              // 需要对比堆顶元素次数，大于 lrumap 的数据就将堆顶数据移出，并将该放入堆中
              Pair<Long, Long> peek = priorityQueue.peek();
              if (peek.getSecond() < v) {
                priorityQueue.poll();
                priorityQueue.add(Pair.of(k, v));
              }
            }
          }

        });

        log.info("统计热数据中热点商品的 top 10 热点商品为：{}", priorityQueue);
        // 将结果上传到 zookeeper 中
        writeTopNToZK(priorityQueue);
        // 每分钟上报一次
        Utils.sleep(60000);
      }

    }

    /**
     * 更新小顶堆中的数据
     *
     * @param priorityQueue 小顶堆
     * @param productId     商品 id
     * @param count         商品出现的次数
     * @return 是否更新
     */
    private boolean updateQueueData(PriorityQueue<Pair<Long, Long>> priorityQueue, Long productId,
                                    Long count) {
      boolean ifDeleted = false;
      Iterator<Pair<Long, Long>> iterator = priorityQueue.iterator();
      while (iterator.hasNext()) {
        Pair<Long, Long> next = iterator.next();
        if (next != null && Objects.equals(next.getFirst(), productId)) {
          iterator.remove();
          ifDeleted = true;
          break;
        }
      }
      if (ifDeleted) {
        priorityQueue.add(Pair.of(productId, count));
      }
      return ifDeleted;
    }

    /**
     * 将 topN 数据写入 zookeeper
     *
     * @param priorityQueue 小顶堆
     */
    private void writeTopNToZK(PriorityQueue<Pair<Long, Long>> priorityQueue) {
      List<Long> products = new ArrayList<>();
      for (Pair<Long, Long> pair : priorityQueue) {
        products.add(pair.getFirst());
      }
      String data = StringUtils.join(products, ",");
      zooKeeperSession.setNodeData(taskNodePath, data);
      log.info("[writeTopNToZK]\t[path:{}, data:{}]", taskNodePath, data);
    }
  }

}
