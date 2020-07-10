package com.liukai.eshop.common.util.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * zookeeper session 类
 */
@Slf4j
public class ZooKeeperSession {

  // 单例类
  private static final ZooKeeperSession instance = new ZooKeeperSession();

  private ZooKeeper zooKeeper;

  private ZooKeeperSession() {
    // 异步连接，使用一个监听器来通知
    // 利用并发工具类 CountDownLatch 让函数等待
    CountDownLatch connectedSemaphore = new CountDownLatch(1);
    try {

      Watcher watcher = new MyWatcher(connectedSemaphore);

      zooKeeper = new ZooKeeper(ZookeeperConstant.CONNECT_STRING, ZookeeperConstant.SESSION_TIMEOUT,
                                watcher);
    } catch (IOException e) {
      e.printStackTrace();
      log.error("ZooKeeperSession 构造器异常", e);
    }

    try {
      // 主线程阻塞等待
      connectedSemaphore.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.info("zookeeper 初始化完毕");
  }

  public static ZooKeeperSession getInstance() {
    return instance;
  }

  /**
   * 构建分布式锁路径
   *
   * @param businessLockPath 业务锁路径
   * @param businessArg      业务参数
   * @return 分布式锁路径
   */
  public static String buildDistributeLockPath(String businessLockPath, String businessArg) {
    return ZookeeperConstant.DISTRIBUTE_LOCAL_BASE_PATH + businessLockPath + businessArg;
  }

  public static void main(String[] args) throws InterruptedException {
    ZooKeeperSession zooKeeperSession = ZooKeeperSession.getInstance();
    int count = 3;
    CountDownLatch countDownLatch = new CountDownLatch(count);

    IntStream.range(0, count).forEach(i -> new Thread(() -> {
      // 获取分布式锁
      zooKeeperSession
        .acquireDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_OF_PRODUCT_PATH, "1");
      log.info(Thread.currentThread().getName() + " 拿到锁并休眠 5 秒钟");
      try {
        TimeUnit.SECONDS.sleep(5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // 释放分布式锁
      zooKeeperSession
        .releaseDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_OF_PRODUCT_PATH, "1");
      log.info(Thread.currentThread().getName() + " 释放锁");
      // 发射
      countDownLatch.countDown();
    }, "线程 t" + i).start());

    countDownLatch.await();
  }

  /**
   * 获取分布式锁
   *
   * @param businessLockPath 业务锁路径
   * @param businessArg      业务参数
   */
  public void acquireDistributedLock(String businessLockPath, String businessArg) {
    String path = buildDistributeLockPath(businessLockPath, businessArg);
    try {
      // 创建一个临时节点，后面有两个参数，一个是安全策略，一个是临时节点类型
      // EPHEMERAL: 客户端断开时，该节点自动被删除
      zooKeeper.create(path, ZookeeperConstant.EMPTY_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                       CreateMode.EPHEMERAL);
      log.info("获取锁成功 product[id=" + businessArg + "]");
    } catch (Exception e) {
      log.info("获取锁失败，原因：" + e.getMessage());
      // 如果锁已经被创建，那么将抛出异常
      // 循环等待锁的释放
      int count = 0;
      while (true) {
        try {
          // 休眠 20 毫秒再尝试创建
          TimeUnit.SECONDS.sleep(5);
          zooKeeper.create(path, ZookeeperConstant.EMPTY_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                           CreateMode.EPHEMERAL);
          log.info("获取锁成功 product[id=" + businessArg + "]" + "尝试了 " + count + " 次");
          break;
        } catch (Exception e1) {
          log.info("获取锁失败，原因：" + e.getMessage());
          count++;
        }
      }
    }

  }

  /**
   * 获取分布式锁
   *
   * @param businessLockPath 业务锁路径
   * @param businessArg      业务参数
   * @return 是否获取成功
   */
  public boolean acquireFastFairDistributedLock(String businessLockPath, String businessArg) {
    String path = buildDistributeLockPath(businessLockPath, businessArg);
    try {
      // 创建一个临时节点，后面有两个参数，一个是安全策略，一个是临时节点类型
      // EPHEMERAL: 客户端断开时，该节点自动被删除
      zooKeeper.create(path, ZookeeperConstant.EMPTY_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                       CreateMode.EPHEMERAL);
      log.info("获取快速失败锁成功 product[id=" + businessArg + "]");
      return true;
    } catch (Exception e) {
      log.info("获取快速失败锁失败，原因：" + e.getMessage());
      // 如果锁已经被创建
      return false;
    }
  }

  /**
   * 释放分布式锁
   *
   * @param businessLockPath 业务锁路径
   * @param businessArg      业务参数
   */
  public void releaseDistributedLock(String businessLockPath, String businessArg) {
    String path = buildDistributeLockPath(businessLockPath, businessArg);
    try {
      zooKeeper.delete(path, -1);
    } catch (InterruptedException | KeeperException e) {
      e.printStackTrace();
    }

  }

  /**
   * 将数据写入指定路径
   *
   * @param path 路径
   * @param data 数据
   */
  public void setNodeData(String path, String data) {
    try {
      // 判断节点是否存在，如果不存在则创建持久存储的节点
      Stat exists = zooKeeper.exists(path, false);
      if (exists == null) {
        zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      } else {
        zooKeeper.setData(path, data.getBytes(), -1);
      }
    } catch (KeeperException | InterruptedException e) {
      log.error("zookeeper 节点数据写入失败，path:{}, data:{}", path, data);
    }
  }

  public String getNodeData(String path) {
    try {
      return new String(zooKeeper.getData(path, false, new Stat()));
    } catch (KeeperException | InterruptedException e) {
      log.error("获取 zookeeper 节点数据异常, path:{}", path);
    }
    return null;
  }

  @Slf4j
  static class MyWatcher implements Watcher {

    CountDownLatch countDownLatch;

    public MyWatcher(CountDownLatch countDownLatch) {
      this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent event) {
      // 获取监听器的事件对象
      Watcher.Event.KeeperState state = event.getState();
      // System.out.println("zookeeper 监听到事件 watch event: " + state);
      // 如果这里使用 lambda 表达式构造事件监听器类，并且在 process 方法中使用用了外围类的 static 声明的日志对象 log（比如 lombok 的 @Slf4j ）打日志时
      // 则会一直阻塞不执行打印日志以下的逻辑代码，
      // 怀疑类加载那块出问题了(具体原因是为什么？在别的类中实现其他线程执行 lambda 生成的类逻辑中使用外围类 static 日志对象打印日志不会阻塞)
      log.info("watch event: " + state);
      // 客户端已经连接上 zookeeper 服务器时创建一个
      if (state == Watcher.Event.KeeperState.SyncConnected) {
        log.info("zookeeper 已经连接");
        // System.out.println("zookeeper 已经连接");
        // 释放闭锁，让主线程可以开始执行自己的逻辑
        countDownLatch.countDown();
      }
    }

  }

}
