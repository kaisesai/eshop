package com.liukai.eshop.cache.ha.command;

import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 多级降级的获取商品命令
 */
@Slf4j
public class GetProductMultipleFallbackCommand extends HystrixCommand<ProductInfo> {

  private final Long productId;

  public GetProductMultipleFallbackCommand(Long productId) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductCommandGroup"))
                // 不同的线程池
                .andThreadPoolKey(
                  HystrixThreadPoolKey.Factory.asKey("GetProductMultipleFallbackCommand"))

         );
    this.productId = productId;
  }

  @Override
  protected String getCacheKey() {
    return String.valueOf(productId);
  }

  @Override
  protected ProductInfo run() throws Exception {
    System.out.println("正常流程获取");
    if (productId == 2) {
      throw new RuntimeException("模拟正常流程获取失败");
    }
    // String url = "http://localhost:7000/getProduct?productId=" + productId;
    // String response = HttpClientUtils.sendGetRequest(url);
    // return JSON.parseObject(response, ProductInfo.class);
    // return null;
    return ProductInfo.getDefaultInstance(productId);
  }

  @Override
  protected ProductInfo getFallback() {
    log.info("正常降级流程");
    return new CommandWithFallbackViaNetwork(productId).execute();
  }

  public static class CommandWithFallbackViaNetwork extends HystrixCommand<ProductInfo> {

    private Long productId;

    protected CommandWithFallbackViaNetwork(Long productId) {
      super(Setter.withGroupKey(
        HystrixCommandGroupKey.Factory.asKey("CommandWithFallbackViaNetworkGroup"))
                  // 不同的线程池
                  .andThreadPoolKey(
                    HystrixThreadPoolKey.Factory.asKey("CommandWithFallbackViaNetworkPool"))

           );
    }

    @Override
    protected ProductInfo run() throws Exception {
      log.info("第一级降级");
      if (productId == 2) {
        throw new RuntimeException("模拟一级策略获取失败");
      }
      // 第一级降级策略：本来是该调用另外一个机房的服务
      // 我们这里没有另外的机房，还是调用原来的服务
      // String url = "http://localhost:7000/getProduct?productId=" + productId;
      // String response = HttpClientUtils.sendGetRequest(url);
      // return JSON.parseObject(response, ProductInfo.class);
      return ProductInfo.getDefaultInstance(productId);
    }

    @Override
    protected ProductInfo getFallback() {
      log.info("第二级降级");
      // 第二级降级策略：使用残缺模式返回数据
      ProductInfo productInfo = new ProductInfo();
      productInfo.setId(productId);
      // 下面的数据可以从本地 ehcache 中获取数据填充后返回
      productInfo.setName("二级降级：残缺数据");
      return productInfo;
    }
  }
}
