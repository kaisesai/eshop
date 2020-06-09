package com.liukai.eshop.cache.service.impl;

import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.model.entity.ProductInfo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

  public static final String CACHE_NAME = "local";

  /**
   * 将商品信息保存到本地缓存中
   */
  @Override
  @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
  public ProductInfo saveLocalCache(ProductInfo productInfo) {
    return productInfo;
  }

  /**
   * 从本地缓存中获取商品信息
   */
  @Override
  @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
  public ProductInfo getLocalCache(Long id) {
    return null;
  }

}
