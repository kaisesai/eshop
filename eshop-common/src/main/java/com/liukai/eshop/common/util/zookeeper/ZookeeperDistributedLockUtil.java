package com.liukai.eshop.common.util.zookeeper;

/**
 * zookeeper 分布式锁工具类
 */
public class ZookeeperDistributedLockUtil {

  /**
   * 获取分布式锁
   *
   * @param businessLockPath
   * @param businessArg
   */
  public static void acquireDistributedLock(String businessLockPath, String businessArg) {
    ZooKeeperSession.getInstance().acquireDistributedLock(businessLockPath, businessArg);
  }

  /**
   * 释放分布式锁
   *
   * @param businessLockPath
   * @param businessArg
   */
  public static void releaseDistributedLock(String businessLockPath, String businessArg) {
    ZooKeeperSession.getInstance().releaseDistributedLock(businessLockPath, businessArg);
  }

}
