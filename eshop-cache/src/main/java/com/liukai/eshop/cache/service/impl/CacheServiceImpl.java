package com.liukai.eshop.cache.service.impl;

import com.liukai.eshop.cache.service.CacheService;
import com.liukai.eshop.common.util.JsonUtils;
import com.liukai.eshop.common.util.RedisUtils;
import com.liukai.eshop.model.entity.ProductInfo;
import com.liukai.eshop.model.entity.ShopInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

  public static final String CACHE_NAME = "local";

  public static final String REDIS_KEY_PREFIX_PRODUCT_INFO = "product_info_";

  public static final String REDIS_KEY_PREFIX_SHOP_INFO = "shop_info_";

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  /**
   * 将商品信息保存到本地缓存中
   *
   * @return
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

  /**
   * 将商品信息保存到本地的ehcache缓存中
   *
   * @return
   */
  @Override
  @CachePut(value = CACHE_NAME, key = "'product_info_'+#productInfo.getId()")
  public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
    return productInfo;
  }

  /**
   * 从本地ehcache缓存中获取商品信息
   */
  @Override
  @Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
  public ProductInfo getProductInfoFromLocalCache(Long productId) {
    return null;
  }

  /**
   * 将店铺信息保存到本地的ehcache缓存中
   */
  @Override
  @CachePut(value = CACHE_NAME, key = "'shop_info_'+#shopInfo.getId()")
  public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
    return shopInfo;
  }

  /**
   * 从本地ehcache缓存中获取店铺信息
   */
  @Override
  @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
  public ShopInfo getShopInfoFromLocalCache(Long shopId) {
    return null;
  }

  @Override
  public void saveProductInfo2RedisCache(ProductInfo productInfo) {
    String key = RedisUtils.generatorValueKey(REDIS_KEY_PREFIX_PRODUCT_INFO, productInfo.getId());
    stringRedisTemplate.opsForValue().set(key, JsonUtils.writeValueAsString(productInfo));
  }

  @Override
  public ProductInfo getProductInfoFromRedisCache(Long productId) {
    String key = RedisUtils.generatorValueKey(REDIS_KEY_PREFIX_PRODUCT_INFO, productId);
    String jsonStr = stringRedisTemplate.opsForValue().get(key);
    return JsonUtils.readValue(jsonStr, ProductInfo.class);
  }

  @Override
  public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
    String key = RedisUtils.generatorValueKey(REDIS_KEY_PREFIX_SHOP_INFO, shopInfo.getId());
    stringRedisTemplate.opsForValue().set(key, JsonUtils.writeValueAsString(shopInfo));
  }

  @Override
  public ShopInfo getShopInfoFromRedisCache(Long shopId) {
    String key = RedisUtils.generatorValueKey(REDIS_KEY_PREFIX_SHOP_INFO, shopId);
    String jsonStr = stringRedisTemplate.opsForValue().get(key);
    return JsonUtils.readValue(jsonStr, ShopInfo.class);
  }

}
