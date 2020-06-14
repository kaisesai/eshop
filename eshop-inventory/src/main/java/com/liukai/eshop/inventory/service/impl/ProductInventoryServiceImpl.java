package com.liukai.eshop.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liukai.eshop.common.util.JsonUtils;
import com.liukai.eshop.common.util.RedisUtils;
import com.liukai.eshop.inventory.mapper.ProductInventoryMapper;
import com.liukai.eshop.inventory.service.ProductInventoryService2;
import com.liukai.eshop.model.entity.ProductInventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 商品库存 service 实现类
 *
 * @author liukai
 */
@Slf4j
@Service
public class ProductInventoryServiceImpl
  extends ServiceImpl<ProductInventoryMapper, ProductInventory>
  implements ProductInventoryService2 {

  public static final String REDIS_CACHE_PREFIX_KEY_PRODUCT_INVENTORY = "cached_product_inventory_";

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Override
  public ProductInventory getCache(Long productId) {
    String key = RedisUtils.generatorValueKey(REDIS_CACHE_PREFIX_KEY_PRODUCT_INVENTORY, productId);
    String value = stringRedisTemplate.opsForValue().get(key);
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return JsonUtils.readValue(value, ProductInventory.class);
  }

  @Override
  public ProductInventory getByProductId(Long productId) {
    LambdaQueryWrapper<ProductInventory> queryWrapper = new LambdaQueryWrapper<ProductInventory>()
      .eq(ProductInventory::getProductId, productId);
    return this.getOne(queryWrapper);
  }

  @Override
  public void removeCache(Long productId) {
    String key = RedisUtils.generatorValueKey(REDIS_CACHE_PREFIX_KEY_PRODUCT_INVENTORY, productId);
    stringRedisTemplate.delete(key);
    log.info("delete product_inventory cache key:{}", key);
  }

  @Override
  public void setCache(ProductInventory productInventory) {
    String key = RedisUtils
      .generatorValueKey(REDIS_CACHE_PREFIX_KEY_PRODUCT_INVENTORY, productInventory.getProductId());
    String jsonString = JsonUtils.writeValueAsString(productInventory);
    stringRedisTemplate.opsForValue().set(key, jsonString);
    log.info("setCache product_inventory cache key:{}", key);
  }

}
