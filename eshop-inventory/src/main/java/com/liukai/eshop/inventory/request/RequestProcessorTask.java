package com.liukai.eshop.inventory.request;

import com.liukai.eshop.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求处理任务
 */
@Slf4j
public class RequestProcessorTask implements Callable<Boolean> {

  private final ArrayBlockingQueue<Request> queue;

  /**
   * key 为商品 id，value 为 true，有更新操作
   */
  private Map<Long, Boolean> flagMap = new ConcurrentHashMap<>();

  public RequestProcessorTask(ArrayBlockingQueue<Request> queue) {
    this.queue = queue;
  }

  @Override
  public Boolean call() {

    while (!Thread.currentThread().isInterrupted()) {

      try {

        Request request = queue.take();
        String requestStr = JsonUtils.writeValueAsString(request);
        log.info("处理请求：{}", requestStr);

        if (!request.isForceRefresh()) {
          // 非强制刷新请求，就是一个正常请求

          if (request instanceof ProductInventoryDBUpdateRequest) {
            // 更新数据库的请求
            flagMap.put(request.getProductId(), true);
            log.info("写请求：{}", requestStr);
          } else if (request instanceof ProductInventoryCacheRefreshRequest) {
            // 缓存刷新请求
            log.info("读请求：{}", requestStr);
            Boolean flag = flagMap.get(request.getProductId());
            if (flag == null) {
              log.info("flag 为 null：{}", requestStr);
              flagMap.put(request.getProductId(), false);
            } else if (flag) {
              // 已经有过读或写请求，并且前面已经有一个写请求了
              // 读取请求，把写请求冲掉
              // 本次读会正常的执行，组成 1+1（1 写 1 读）
              // 后续的正常读请求会被过滤掉
              flagMap.put(request.getProductId(), false);
              log.info("1+1 达成，1 写 1 读：{}", requestStr);
            } else {
              // 如果是读请求，请求前面没有写请求
              log.info("已有 1+1，放弃处理该次请求：{}", requestStr);
              continue;
            }

          }
        }
        request.process();
      } catch (Exception e) {
        log.error("请求任务中断", e);
      }

    }

    return false;
  }

}
