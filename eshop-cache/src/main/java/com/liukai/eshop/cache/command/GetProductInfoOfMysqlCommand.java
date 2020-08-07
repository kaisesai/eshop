package com.liukai.eshop.cache.command;

import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetProductInfoOfMysqlCommand extends HystrixCommand<ProductInfo> {

  private final Long productId;

  public GetProductInfoOfMysqlCommand(Long productId) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductInfoOfMysqlCommand"))
                .andThreadPoolPropertiesDefaults(
                  HystrixThreadPoolProperties.Setter().withCoreSize(30).withMaxQueueSize(5)));
    this.productId = productId;
  }

  @Override
  protected ProductInfo run() throws Exception {
    log.info("[GetProductInfoOfMysqlCommand 执行]\t[productId:{}]", productId);
    return ProductInfo.getDefaultInstance(productId);
  }

  @Override
  protected ProductInfo getFallback() {
    log.info("[GetProductInfoOfMysqlCommand 执行降级]\t[productId:{}]", productId);
    // 至于降级怎么做，下一章节会讲解
    // 本人也希望能在下一章能讲解到稍微真实的一点场景处理
    // 业务在本缓存架构代码中，假定是能百分比获取到商品信息的，如果被 reject 了，那么该怎么办？
    return null;
  }
}
