package com.liukai.eshop.cache.service;

import com.liukai.eshop.model.entity.ProductInfo;
import com.liukai.eshop.model.entity.ShopInfo;

public interface CacheService {

  ProductInfo saveLocalCache(ProductInfo productInfo);

  ProductInfo getLocalCache(Long id);

  ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

  ProductInfo getProductInfoFromLocalCache(Long productId);

  ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

  ShopInfo getShopInfoFromLocalCache(Long shopId);

  void saveProductInfo2RedisCache(ProductInfo productInfo);

  ProductInfo getProductInfoFromRedisCache(Long productId);

  void saveShopInfo2RedisCache(ShopInfo shopInfo);

  ShopInfo getShopInfoFromRedisCache(Long shopId);

}
