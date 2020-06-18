package com.liukai.eshop.cache.kafka;

import com.liukai.eshop.cache.kafka.message.ProductMessage;
import com.liukai.eshop.cache.kafka.message.ShopMessage;
import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.model.entity.ProductInfo;
import com.liukai.eshop.model.entity.ShopInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    // String productInfoJSON
    //   = "{\"productId\": 1, "
    //   + "\"name\": \"iphone7手机\", "
    //   + "\"price\": 5599, "
    //   + "\"pictureList\":\"a.jpg,b.jpg\", "
    //   + "\"specification\": \"iphone7的规格\", "
    //   + "\"service\": \"iphone7的售后服务\","
    //   + " \"color\": \"红色,白色,黑色\","
    //   + " \"size\": \"5.5\", "
    //   + "\"shopId\": 1}";
    ProductInfo productInfo = new ProductInfo();
    productInfo.setId(productId);
    productInfo.setName("iphone7手机");
    productInfo.setPrice(5599L);
    productInfo.setPictureList("a.jpg,b.jpg");
    productInfo.setSpecification("iphone7的规格");
    productInfo.setService("iphone7的售后服务");
    productInfo.setColor("红色,白色,黑色");
    productInfo.setSize("5.5");
    productInfo.setShopId(1L);

    // 保存本地缓存

    log.info("保存 productInfo 数据到缓存：" + productInfo);
    cacheService.saveProductInfo2LocalCache(productInfo);
    log.info("从本地缓存获取 productInfo：" + cacheService.getProductInfoFromLocalCache(productId));
    // 保存 redis 缓存
    cacheService.saveProductInfo2RedisCache(productInfo);
    log.info("从redis缓存中 productInfo 数据到：" + cacheService.getProductInfoFromRedisCache(productId));
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
