package com.liukai.eshop.inventory.request;

/**
 * 抽象的请求接口
 */
public interface Request {

  /**
   * 处理请求
   */
  void process();

  Long getProductId();

  boolean isForceRefresh();

}
