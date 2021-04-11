package com.liukai.eshop.cache.controller;

import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.common.util.zookeeper.ZookeeperConstant;
import com.liukai.eshop.common.util.zookeeper.ZookeeperDistributedLockUtil;
import com.liukai.eshop.model.entity.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 重建缓存：一个队列对应一个消费线程
 */
@Slf4j
@Component
public class RebuildCache {

  private final ArrayBlockingQueue<ProductInfo> blockingQueue = new ArrayBlockingQueue<>(100);

  private final CacheService cacheService;

  public RebuildCache(CacheService cacheService) {
    this.cacheService = cacheService;
    start();
  }

  public void put(ProductInfo productInfo) {
    try {
      blockingQueue.put(productInfo);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public ProductInfo take() {
    try {
      return blockingQueue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void start() {
    // 这里可以使用 spring 的异步注解线程，方便开发
    // 启动一个线程负责消费数据
    new Thread(() -> {
      while (true) {
        try {
          ProductInfo productInfo = blockingQueue.take();
          Long productId = productInfo.getId();
          // 获取分布式锁
          ZookeeperDistributedLockUtil
            .acquireDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_OF_PRODUCT_PATH,
                                    String.valueOf(productId));
          try {
            // 模拟停顿
            TimeUnit.SECONDS.sleep(5);

            // 先从 redis 中获取商品信息
            ProductInfo existedProduct = cacheService.getProductInfoFromRedisCache(productId);
            if (existedProduct != null) {
              // 比较获取到的消息版本与 redis 中的数据版本
              Date existedUpdateTime = existedProduct.getUpdateTime();
              Date updateTime = productInfo.getUpdateTime();
              log.info("【构建缓存】商品信息 productId:{} 本次更新的数据 updateTime:{}, redis 中的数据 updateTime:{}",
                       productId, updateTime, existedUpdateTime);

              // 如果获取的数据版本新则更新
              if (existedUpdateTime == null || updateTime.after(existedUpdateTime)) {
                cacheService.saveProductInfo2LocalCache(productInfo);
                log.info("【构建缓存】最新数据覆盖 redis 中的数据 productInfo：" + cacheService
                  .getProductInfoFromLocalCache(productId));
                // 保存 redis 缓存
                cacheService.saveProductInfo2RedisCache(productInfo);
              } else {
                log.info("【构建缓存】redis 中的数据已经是最新的，本次不进行更新操作");
              }

            } else {
              cacheService.saveProductInfo2LocalCache(productInfo);
              log.info(
                "【构建缓存】构建缓存成功 productInfo：" + cacheService.getProductInfoFromLocalCache(productId));
              // 保存 redis 缓存
              cacheService.saveProductInfo2RedisCache(productInfo);
            }

          } finally {
            ZookeeperDistributedLockUtil
              .releaseDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_OF_PRODUCT_PATH,
                                      String.valueOf(productId));
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

}
