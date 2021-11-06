// package com.liukai.eshop.cache.controller;
//
// import com.liukai.eshop.cache.command.GetProductInfoOfMysqlCommand;
// import com.liukai.eshop.cache.kafka.MsgProducer;
// import com.liukai.eshop.cache.kafka.message.ProductMessage;
// import com.liukai.eshop.cache.kafka.message.ShopMessage;
// import com.liukai.eshop.cache.service.CacheService;
// import com.liukai.eshop.common.config.web.CommonApiResult;
// import com.liukai.eshop.model.entity.ProductInfo;
// import com.liukai.eshop.model.entity.ShopInfo;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.web.bind.annotation.*;
//
// @CommonApiResult
// @Slf4j
// @RequestMapping(value = "/cache")
// @RestController
// public class CacheController {
//
//   @Autowired
//   private CacheService cacheService;
//
//   @Autowired
//   private MsgProducer msgProducer;
//
//   @Autowired
//   private RebuildCache rebuildCache;
//
//   @Value("${app.topics.product}")
//   private String productTopic;
//
//   @Value("${app.topics.shop}")
//   private String shopTopic;
//
//   @PostMapping(value = "/testPutCache")
//   public String testPutCache(@RequestBody ProductInfo productInfo) {
//     cacheService.saveLocalCache(productInfo);
//     return "success";
//   }
//
//   @GetMapping(value = "/testGetCache")
//   public ProductInfo testGetCache(@RequestParam Long id) {
//     return cacheService.getLocalCache(id);
//   }
//
//   @PostMapping(value = "/testProduceMsg")
//   public boolean testProduceMsg(@RequestBody ProductMessage message) {
//     msgProducer.produceMsg(productTopic, message);
//     return true;
//   }
//
//   @PostMapping(value = "/testShopMsg")
//   public boolean testProduceMsg(@RequestBody ShopMessage message) {
//     msgProducer.produceMsg(shopTopic, message);
//     return true;
//   }
//
//   @GetMapping(value = "/testGetProductInfoCache")
//   public ProductInfo testGetProductInfoCache(@RequestParam Long id) {
//     return cacheService.getProductInfoFromLocalCache(id);
//   }
//
//   @GetMapping(value = "/testGetProductInfoFromRedisCache")
//   public ProductInfo testGetProductInfoFromRedisCache(@RequestParam Long id) {
//     return cacheService.getProductInfoFromRedisCache(id);
//   }
//
//   /**
//    * 解决高并发情况下的缓存重建的问题
//    * <p>
//    * 这里的代码别看着奇怪，简单回顾下之前的流程：
//    * 1. 获取 redis 缓存
//    * 2. 获取不到再获取服务的堆缓存（也就是这里的 ehcache）
//    * 3. 还获取不到就需要去数据库获取并重建缓存
//    */
//   @GetMapping(value = "/getProductInfoAndRebuild")
//   public ProductInfo getProductInfo(@RequestParam(value = "product_id") Long productId) {
//     ProductInfo productInfo = cacheService.getProductInfoFromRedisCache(productId);
//     log.info("从 redis 中获取商品信息 productInfo:{}", productInfo);
//     if (productInfo == null) {
//       productInfo = cacheService.getProductInfoFromLocalCache(productId);
//       log.info("从 ehcache 中获取商品信息 productInfo:{}", productInfo);
//     }
//     if (productInfo == null) {
//       // 两级缓存中都获取不到数据，那么就需要从数据源重新拉取数据，重建缓存
//       // 但是这里暂时不讲
//       log.info("缓存重建 商品信息");
//       // 假设这里从数据库中获取了数据
//       GetProductInfoOfMysqlCommand command = new GetProductInfoOfMysqlCommand(productId);
//       productInfo = command.execute();
//       // 这里是阻塞请求，如果队列已满，则会该线程被阻塞，应该用异步线程
//       rebuildCache.put(productInfo);
//     }
//     return productInfo;
//   }
//
//   @GetMapping(value = "/getShopInfo")
//   public ShopInfo getShopInfo(@RequestParam(value = "shop_id") Long shopId) {
//     ShopInfo shopInfo = cacheService.getShopInfoFromRedisCache(shopId);
//     log.info("从 redis 中获取店铺信息");
//     if (shopInfo == null) {
//       shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
//       log.info("从 ehcache 中获取店铺信息");
//     }
//     if (shopInfo == null) {
//       // 两级缓存中都获取不到数据，那么就需要从数据源重新拉取数据，重建缓存
//       // 但是这里暂时不讲
//       log.info("缓存重建 店铺信息");
//     }
//     return shopInfo;
//   }
//
// }
