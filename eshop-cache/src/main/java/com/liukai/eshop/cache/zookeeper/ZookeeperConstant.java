package com.liukai.eshop.cache.zookeeper;

/**
 * zookeeper 常量类
 */
public class ZookeeperConstant {

  /**
   * zookeeper 服务地址
   */
  public static final String CONNECT_STRING = "192.168.1.106:2181";

  /**
   * 会话连接超时时间
   */
  public static final int SESSION_TIMEOUT = 5000;

  /**
   * 分布式锁的基础路径
   */
  public static final String DISTRIBUTE_LOCAL_BASE_PATH = "/distribute-lock";

  /**
   * 分布式锁——商品锁路径
   */
  public static final String DISTRIBUTE_LOCAL_OF_PRODUCT_PATH = "/product-lock-";

  /**
   * 分布式锁的数据
   */
  public static final byte[] EMPTY_DATA = "".getBytes();

}
