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
 * 从 redis 中读取商品信息的 command
 */
@Slf4j
public class GetProductInfoFromRedisCacheCommand extends HystrixCommand<ProductInfo> {

  private final Long productId;

  private final StringRedisTemplate stringRedisTemplate;

  public GetProductInfoFromRedisCacheCommand(Long productId,
                                             StringRedisTemplate stringRedisTemplate) {
    super(Setter.withGroupKey(
      HystrixCommandGroupKey.Factory.asKey("GetProductInfoFromRedisCacheCommand")));
    this.productId = productId;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected ProductInfo run() throws Exception {
    log.info("[GetProductInfoFromRedisCacheCommand 执行]\t[productId:{}]", productId);
    String key = RedisUtils
      .generatorValueKey(CacheConstant.REDIS_KEY_PREFIX_PRODUCT_INFO, productId);
    String jsonStr = stringRedisTemplate.opsForValue().get(key);
    return JsonUtils.readValue(jsonStr, ProductInfo.class);
  }

  @Override
  protected ProductInfo getFallback() {
    log.warn("[GetProductInfoFromRedisCacheCommand 执行降级]\t[productId:{}]", productId);
    return null;
  }
}
