package com.liukai.eshop.inventory.service;

import com.liukai.eshop.inventory.request.Request;

/**
 * 请求异步执行 service
 */
public interface RequestAsyncProcessService {

  /**
   * 执行请求
   *
   * @param request
   */
  void process(Request request);

}
