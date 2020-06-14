package com.liukai.eshop.cache.controller;

import com.liukai.eshop.cache.kafka.MsgProducer;
import com.liukai.eshop.cache.kafka.message.ProductMessage;
import com.liukai.eshop.cache.kafka.message.ShopMessage;
import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.model.entity.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/cache")
@RestController
public class CacheController {

  @Autowired
  private CacheService cacheService;

  @Autowired
  private MsgProducer msgProducer;

  @Value("${app.topics.product}")
  private String productTopic;

  @Value("${app.topics.shop}")
  private String shopTopic;

  @PostMapping(value = "/testPutCache")
  public String testPutCache(@RequestBody ProductInfo productInfo) {
    cacheService.saveLocalCache(productInfo);
    return "success";
  }

  @GetMapping(value = "/testGetCache")
  public ProductInfo testGetCache(@RequestParam Long id) {
    return cacheService.getLocalCache(id);
  }

  @PostMapping(value = "/testProduceMsg")
  public boolean testProduceMsg(@RequestBody ProductMessage message) {
    msgProducer.produceMsg(productTopic, message);
    return true;
  }

  @PostMapping(value = "/testShopMsg")
  public boolean testProduceMsg(@RequestBody ShopMessage message) {
    msgProducer.produceMsg(shopTopic, message);
    return true;
  }


  @GetMapping(value = "/testGetProductInfoCache")
  public ProductInfo testGetProductInfoCache(@RequestParam Long id) {
    return cacheService.getProductInfoFromLocalCache(id);
  }



  @GetMapping(value = "/testGetProductInfoFromRedisCache")
  public ProductInfo testGetProductInfoFromRedisCache(@RequestParam Long id) {
    return cacheService.getProductInfoFromRedisCache(id);
  }


}
