package com.liukai.eshop.cache.command;

import com.liukai.eshop.cache.constant.CacheConstant;
import com.liukai.eshop.common.util.JsonUtils;
import com.liukai.eshop.common.util.RedisUtils;
import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 保存商品信息到 redis 的 command
 */
@Slf4j
public class SaveProductInfo2RedisCommand extends HystrixCommand<Boolean> {

  private final ProductInfo productInfo;

  private final StringRedisTemplate stringRedisTemplate;

  public SaveProductInfo2RedisCommand(ProductInfo productInfo,
                                      StringRedisTemplate stringRedisTemplate) {
    super(
      Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SaveProductInfo2RedisCommand")));
    this.productInfo = productInfo;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected Boolean run() throws Exception {
    log.info("[SaveProductInfo2RedisCommand 执行]\t[productInfo:{}]",
             JsonUtils.writeValueAsString(productInfo));
    String key = RedisUtils
      .generatorValueKey(CacheConstant.REDIS_KEY_PREFIX_PRODUCT_INFO, productInfo.getId());
    stringRedisTemplate.opsForValue().set(key, JsonUtils.writeValueAsString(productInfo));
    return true;
  }

  @Override
  protected Boolean getFallback() {
    log.info("[SaveProductInfo2RedisCommand 执行降级]\t[productInfo:{}]",
             JsonUtils.writeValueAsString(productInfo));
    return false;
  }
}
