package com.liukai.eshop.cache.task;

import com.liukai.eshop.cache.SpringContextUtil;
import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.common.util.zookeeper.ZooKeeperSession;
import com.liukai.eshop.common.util.zookeeper.ZookeeperConstant;
import com.liukai.eshop.model.entity.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 缓存预热任务
 */
@Slf4j
public class CachePrewarmTask implements Runnable {

  public static final String TASK_NODE_LOCK_PATH = "/hot-product-task-lock-";

  public static final String TASK_NODE_PREWARM_STATE_PATH = "/hot-product-task-prewarm-state";

  @Override
  public void run() {
    ZooKeeperSession zk = ZooKeeperSession.getInstance();
    // 1. 获取 task id 列表
    String taskListNodeData = zk.getNodeData(ZookeeperConstant.STORM_TASK_LIST_NODE);
    if (StringUtils.isBlank(taskListNodeData)) {
      log.warn("task list is empty!");
      return;
    }

    WebApplicationContext context = SpringContextUtil.getWebApplicationContext();
    CacheService cacheService = context.getBean(CacheService.class);

    Set<String> taskIds = Arrays.stream(StringUtils.split(taskListNodeData, ",")).distinct()
                                .collect(Collectors.toSet());
    for (String taskId : taskIds) {
      // 获取 task id 路径下的锁

      // 如果获取失败，说明被其他服务实例预热了
      if (!zk.acquireFastFairDistributedLock(TASK_NODE_LOCK_PATH, taskId)) {
        continue;
      }

      // 获取检查预热状态
      String taskNodePrewarmStatePath = TASK_NODE_PREWARM_STATE_PATH + taskId;

      String taskNodePrewarmState = zk.getNodeData(taskNodePrewarmStatePath);

      // 已经预热过了
      if (StringUtils.isNotBlank(taskNodePrewarmState)) {
        zk.releaseDistributedLock(TASK_NODE_LOCK_PATH, taskId);
        log.info("缓存已经预热过了，zk path: " + TASK_NODE_LOCK_PATH + taskId);
        continue;
      }

      // 还未被预热过，读取 topn 列表，并从数据源中获取商品数据，存入缓存
      String nodeData = zk.getNodeData(ZookeeperConstant.STORM_HOT_PRODUCT_TASK_PATH + taskId);
      // 没有数据不作处理
      if (StringUtils.isBlank(nodeData)) {
        zk.releaseDistributedLock(TASK_NODE_LOCK_PATH, taskId);
        continue;
      }

      // 解析task id 路径下的商品 id 数据
      List<Long> productIds = Arrays.stream(StringUtils.split(nodeData, ",")).map(Long::parseLong)
                                    .collect(Collectors.toList());

      // 假设这里从数据库中获取数据
      productIds.forEach(productId -> {
        ProductInfo productInfo = ProductInfo.getDefaultInstance(productId);
        log.info("预热缓存数据：{}", productInfo);
        cacheService.saveProductInfo2LocalCache(productInfo);
        cacheService.saveProductInfo2RedisCache(productInfo);
      });

      // 修改预热状态
      zk.setNodeData(taskNodePrewarmStatePath, "success");

      // 释放该 task 节点的锁
      zk.releaseDistributedLock(TASK_NODE_LOCK_PATH, taskId);
    }

  }

}
