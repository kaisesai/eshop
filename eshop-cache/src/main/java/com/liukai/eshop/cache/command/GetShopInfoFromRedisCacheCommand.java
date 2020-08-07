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
 * 从 redis 中获取店铺信息 command
 */
@Slf4j
public class GetShopInfoFromRedisCacheCommand extends HystrixCommand<ShopInfo> {

  private final Long shopId;

  private final StringRedisTemplate stringRedisTemplate;

  public GetShopInfoFromRedisCacheCommand(Long shopId, StringRedisTemplate stringRedisTemplate) {
    super(Setter.withGroupKey(
      HystrixCommandGroupKey.Factory.asKey("GetShopInfoFromRedisCacheCommand")));
    this.shopId = shopId;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected ShopInfo run() throws Exception {
    log.info("[GetShopInfoFromRedisCacheCommand 执行]\t[shopId:{}]", shopId);
    String key = RedisUtils.generatorValueKey(CacheConstant.REDIS_KEY_PREFIX_SHOP_INFO, shopId);
    String jsonStr = stringRedisTemplate.opsForValue().get(key);
    return JsonUtils.readValue(jsonStr, ShopInfo.class);
  }

  @Override
  protected ShopInfo getFallback() {
    log.info("[GetShopInfoFromRedisCacheCommand 执行降级]\t[shopId:{}]", shopId);
    return ShopInfo.builder().name("无").level(-1).goodCommentRate(0.00).build();
  }
}
