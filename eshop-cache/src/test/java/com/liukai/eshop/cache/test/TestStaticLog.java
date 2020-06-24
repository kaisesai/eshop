package com.liukai.eshop.cache.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestStaticLog {

  public static final Logger LOGGER = LoggerFactory.getLogger(TestStaticLog.class);

  private CountDownLatch countDownLatch = new CountDownLatch(1);

  public TestStaticLog() {
    LOGGER.info("启动构造器");
    // 创建一个异步线程
    new Thread(()->{
      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      LOGGER.info("子线程日志");
      countDownLatch.countDown();
    }).start();

    try {
      LOGGER.info("主线程阻塞");
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    LOGGER.info("构造器启动");
  }

  public static void main(String[] args) {
    TestStaticLog testStaticLog =new TestStaticLog();
  }

}
