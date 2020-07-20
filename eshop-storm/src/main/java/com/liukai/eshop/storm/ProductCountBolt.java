package com.liukai.eshop.storm;

import com.alibaba.fastjson.JSON;
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

import java.util.*;

/**
 * 商品id 统计 bolt
 */
@Slf4j
public class ProductCountBolt extends BaseRichBolt {

  public static final int TOP_N = 3;

  /**
   * LRU 缓存，其中 key 为商品 id，value 为出现的次数
   */
  private final LRUMap<Long, Long> countMap = new LRUMap<>(100);

  private ZooKeeperSession zooKeeperSession;

  private int taskId = -1;

  private String taskNodePath;

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
    taskId = context.getThisTaskId();

    taskNodePath = ZookeeperConstant.STORM_HOT_PRODUCT_TASK_PATH + taskId;

    // 启动一个线程每分钟计算一次 topN
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
    zooKeeperSession.acquireDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_BASE_PATH,
                                            ZookeeperConstant.STORM_HOT_PRODUCT_TASK_LIST_LOCK);
    // 获取任务节点路径的数据
    String nodeData = zooKeeperSession.getNodeData(ZookeeperConstant.STORM_TASK_LIST_NODE);
    // 如果已经存在，把自己的 taskid 追加到尾部
    if (StringUtils.isNotBlank(nodeData)) {
      nodeData += ("," + taskId);
    } else {
      nodeData = String.valueOf(taskId);
    }
    // 写入任务节点数据
    zooKeeperSession.setNodeData(ZookeeperConstant.STORM_TASK_LIST_NODE, nodeData);
    // 释放锁
    zooKeeperSession.releaseDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_BASE_PATH,
                                            ZookeeperConstant.STORM_HOT_PRODUCT_TASK_LIST_LOCK);
  }

  private void startTopNThread() {
    new Thread(() -> {

      // 优先级队列（小顶堆），用于查询 topn，Pair<Long, Long> left 为商品 id，right 为出现的次数
      PriorityQueue<Pair<Long, Long>> priorityQueue = new PriorityQueue<>(TOP_N, Comparator
        .comparingLong(Pair::getSecond));

      while (true) {
        // 每分钟检查一次，堆中的数据
        countMap.forEach((k, v) -> {
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
        Utils.sleep(60000);
      }

    }).start();
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
    String data = JSON.toJSONString(products);
    zooKeeperSession.setNodeData(taskNodePath, data);
    log.info("[writeTopNToZK]\t[path:{}, data:{}]", taskNodePath, data);
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

}
