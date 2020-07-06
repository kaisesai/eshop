package com.liukai.eshop.common.util.zookeeper;

/**
 * zookeeper 常量类
 */
public class ZookeeperConstant {

  /**
   * zookeeper 服务地址
   */
  public static final String CONNECT_STRING = "127.0.0.1:2181";

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
   * 分布式锁——storm 冷启动任务 id 锁
   */
  public static final String STORM_HOT_PRODUCT_TASK_PATH = "/hot-product-task-";

  /**
   * 冷启动热点数据分布式锁
   */
  public static final String STORM_HOT_PRODUCT_TASK_LIST_LOCK = "/hot-product-task-list-lock";

  /**
   * 冷启动热点数据任务节点路径
   */
  public static final String STORM_TASK_LIST_NODE = "/hot-product-task-list";

  /**
   * 分布式锁的数据
   */
  public static final byte[] EMPTY_DATA = "".getBytes();

}
