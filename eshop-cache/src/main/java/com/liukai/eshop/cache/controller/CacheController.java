package com.liukai.eshop.cache.controller;

import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.model.entity.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/cache")
@RestController
public class CacheController {

  @Autowired
  private CacheService cacheService;

  @PostMapping(value = "/testPutCache")
  public String testPutCache(@RequestBody ProductInfo productInfo) {
    cacheService.saveLocalCache(productInfo);
    return "success";
  }

  @GetMapping(value = "/testGetCache")
  public ProductInfo testGetCache(@RequestParam Long id) {
    return cacheService.getLocalCache(id);
  }

}
