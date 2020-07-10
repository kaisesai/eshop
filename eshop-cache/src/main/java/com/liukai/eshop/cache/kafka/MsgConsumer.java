package com.liukai.eshop.cache.kafka;

import com.liukai.eshop.cache.kafka.message.ProductMessage;
import com.liukai.eshop.cache.kafka.message.ShopMessage;
import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.common.util.zookeeper.ZookeeperConstant;
import com.liukai.eshop.common.util.zookeeper.ZookeeperDistributedLockUtil;
import com.liukai.eshop.model.entity.ProductInfo;
import com.liukai.eshop.model.entity.ShopInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MsgConsumer {

  @Autowired
  private CacheService cacheService;

  @KafkaListener(id = "listener-1", topics = "${app.topics.product}", groupId = "group-1")
  public void listenProductTopic(ProductMessage msg) {
    log.info("group-1 监听到 product topic 消息，开始消费：" + msg.toString());
    processProductInfoChangeMessage(msg);
  }

  @KafkaListener(id = "listener-2", topics = "${app.topics.shop}", groupId = "group-1")
  public void listenShopTopic(ShopMessage msg) {
    log.info("group-1 监听到 shop topic 消息，开始消费：" + msg.toString());
    processShopInfoChangeMessage(msg);
  }

  /**
   * 处理商品信息变更消息
   *
   * @param message 商品信息
   */
  private void processProductInfoChangeMessage(ProductMessage message) {

    // 商品 productId
    Long productId = message.getProductId();

    // 模拟查询数商品服务信息
    // 这里写死
    ProductInfo productInfo = ProductInfo.getDefaultInstance(productId);

    // 生成随机数
    int nextInt = RandomUtils.nextInt(0, 1000);
    Date date = DateUtils.addSeconds(new Date(), -nextInt);
    productInfo.setUpdateTime(date);

    ZookeeperDistributedLockUtil
      .acquireDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_OF_PRODUCT_PATH,
                              String.valueOf(productId));
    try {
      TimeUnit.SECONDS.sleep(5);

      // 先从 redis 中查询下是否已经被放入数据
      ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productId);
      if (existedProductInfo != null) {
        // 比较获取到的消息版本与 redis 中的数据版本
        Date existedUpdateTime = existedProductInfo.getUpdateTime();
        Date updateTime = productInfo.getUpdateTime();
        log.info("【更新商品信息】商品信息 productId:{} 本次更新的数据 updateTime:{}, redis 中的数据 updateTime:{}",
                 productId, updateTime, existedUpdateTime);

        // 如果获取的数据版本新则更新
        if (existedUpdateTime == null || updateTime.after(existedUpdateTime)) {
          cacheService.saveProductInfo2LocalCache(productInfo);
          log.info("【更新商品信息】最新数据覆盖 redis 中的数据 productInfo：" + cacheService
            .getProductInfoFromLocalCache(productId));
          // 保存 redis 缓存
          cacheService.saveProductInfo2RedisCache(productInfo);
        } else {
          log.info("【更新商品信息】redis 中的数据已经是最新的，本次不进行更新操作");
        }

      } else {
        cacheService.saveProductInfo2LocalCache(productInfo);
        log.info("【更新商品信息】获取刚保存到本地的缓存 productInfo：" + cacheService
          .getProductInfoFromLocalCache(productId));
        // 保存 redis 缓存
        cacheService.saveProductInfo2RedisCache(productInfo);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      ZookeeperDistributedLockUtil
        .releaseDistributedLock(ZookeeperConstant.DISTRIBUTE_LOCAL_OF_PRODUCT_PATH,
                                String.valueOf(productId));
    }

  }

  /**
   * 处理店铺信息变更消息
   *
   * @param message 店铺消息
   */
  private void processShopInfoChangeMessage(ShopMessage message) {
    // 提取出商品id
    Long shopId = message.getShopId();
    // 这里也是模拟去数据库获取到了信息

    // String shopInfoJSON
    //   = "{\"id\": 1, "
    //   + "\"name\": \"小王的手机店\", "
    //   + "\"level\": 5, "
    //   + "\"goodCommentRate\":0.99}";
    ShopInfo shopInfo = new ShopInfo();
    shopInfo.setId(shopId);
    shopInfo.setName("小王的手机店");
    shopInfo.setLevel(5);
    shopInfo.setGoodCommentRate(0.99D);

    log.info("保存 shopInfo 数据到缓存：" + shopInfo);
    cacheService.saveShopInfo2LocalCache(shopInfo);
    log.info("从本地缓存的获取 shopInfo：{}", cacheService.getShopInfoFromLocalCache(shopId));

    // 保存 redis 缓存
    cacheService.saveShopInfo2RedisCache(shopInfo);
    log.info("从redis缓存获取 shopInfo：{}", cacheService.getShopInfoFromRedisCache(shopId));
  }

  // @KafkaListener(id = "listener-2", topics = "${app.topics.product}", groupId = "group-2")
  // public void listen2(ConsumerRecord<?, ?> cr) {
  //   log.info("group-2 监听到消息，开始消费：" + cr.toString());
  //
  // }

}
