package com.liukai.eshop.inventory.service.impl;

import com.liukai.eshop.inventory.request.Request;
import com.liukai.eshop.inventory.request.RequestQueue;
import com.liukai.eshop.inventory.service.RequestAsyncProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
@Service
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

  @Autowired
  private RequestQueue requestQueue;

  @Override
  public void process(Request request) {

    // 1. 根据商品 id 路由到具体的队列
    ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());

    // 2. 放入队列
    try {
      queue.put(request);
    } catch (InterruptedException e) {
      log.error("请求处理 service 处理异常", e);
    }

  }

  private ArrayBlockingQueue<Request> getRoutingQueue(Long productId) {
    // 获取 productId 的 hash 值
    String key = String.valueOf(productId);

    int h;
    int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);

    // 对hash值取模，将hash值路由到指定的内存队列中，比如内存队列大小8
    // 用内存队列的数量对hash值取模之后，结果一定是在0~7之间
    // 所以任何一个商品id都会被固定路由到同样的一个内存队列中去的
    int index = (requestQueue.queueSize() - 1) & hash;
    return requestQueue.getQueue(index);
  }

}
