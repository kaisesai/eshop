package com.liukai.eshop.cache.command;

import com.liukai.eshop.cache.constant.CacheConstant;
import com.liukai.eshop.common.util.JsonUtils;
import com.liukai.eshop.common.util.RedisUtils;
import com.liukai.eshop.model.entity.ShopInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 保存店铺信息到 redis 的 command
 */
@Slf4j
public class SaveShopInfo2RedisCacheCommand extends HystrixCommand<Boolean> {

  private final ShopInfo shopInfo;

  private final StringRedisTemplate stringRedisTemplate;

  public SaveShopInfo2RedisCacheCommand(ShopInfo shopInfo,
                                        StringRedisTemplate stringRedisTemplate) {
    super(
      Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SaveShopInfo2RedisCacheCommand")));
    this.shopInfo = shopInfo;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected Boolean run() throws Exception {
    log.info("[SaveShopInfo2RedisCacheCommand 执行]\t[shopInfo:{}]",
             JsonUtils.writeValueAsString(shopInfo));
    String key = RedisUtils
      .generatorValueKey(CacheConstant.REDIS_KEY_PREFIX_SHOP_INFO, shopInfo.getId());
    stringRedisTemplate.opsForValue().set(key, JsonUtils.writeValueAsString(shopInfo));
    return true;
  }

  @Override
  protected Boolean getFallback() {
    log.info("[SaveShopInfo2RedisCacheCommand 执行降级]\t[shopInfo:{}]",
             JsonUtils.writeValueAsString(shopInfo));
    return false;
  }
}
