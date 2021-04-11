package com.liukai.eshop.cache.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TestStaticLog {
  
  public static final Logger LOGGER = LoggerFactory.getLogger(TestStaticLog.class);
  
  private final CountDownLatch countDownLatch = new CountDownLatch(1);
  
  // public TestStaticLog() {
  //   LOGGER.info("启动构造器");
  //   // 创建一个异步线程
  //   new Thread(()->{
  //     try {
  //       TimeUnit.SECONDS.sleep(3);
  //     } catch (InterruptedException e) {
  //       e.printStackTrace();
  //     }
  //
  //     LOGGER.info("子线程日志");
  //     countDownLatch.countDown();
  //   }).start();
  //
  //   try {
  //     LOGGER.info("主线程阻塞");
  //     countDownLatch.await();
  //   } catch (InterruptedException e) {
  //     e.printStackTrace();
  //   }
  //
  //   LOGGER.info("构造器启动");
  // }
  
  @Test
  public void testLinkedHashMap() {
    LruCache<Integer, Integer> map = new LruCache<>(3);
    map.put(1, 1);
    map.put(2, 1);
    map.put(3, 1);
    map.put(4, 1);
    
    // map.
    
    // Integer integer = map.get(3);
    // System.out.println("访问时间："+integer);
    
    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
      System.out.print(entry.getKey() + " ");
    }
    System.out.println("访问结束");
  }
  
  static class LruCache<K, V> extends LinkedHashMap<K, V> {
    
    private final int maxSize;
    
    public LruCache(int maxSize) {
      super(maxSize, 0.75F, true);
      this.maxSize = maxSize;
    }
    
    /**
     * 重写插入之后的执行方法
     *
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
      return super.size() > maxSize;
    }
    
  }
  
  // public static void main(String[] args) {
  //   TestStaticLog testStaticLog =new TestStaticLog();
  // }
  
}
