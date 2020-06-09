package com.liukai.eshop.cache.service;

import com.liukai.eshop.model.entity.ProductInfo;

public interface CacheService {

  ProductInfo saveLocalCache(ProductInfo productInfo);

  ProductInfo getLocalCache(Long id);

}
