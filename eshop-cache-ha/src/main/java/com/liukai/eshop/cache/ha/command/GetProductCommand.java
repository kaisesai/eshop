package com.liukai.eshop.cache.ha.command;

import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 获取商品信息命令
 */
@Slf4j
public class GetProductCommand extends HystrixCommand<ProductInfo> {

  private final Long productId;

  public GetProductCommand(Long productId) {
    super(
      // 线程组名
      Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductCommandGroup"))
            // 命令参数
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                  // 超时时间
                                                                  .withExecutionTimeoutInMilliseconds(
                                                                    6000))
            // 线程池参数
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                                                        // 线程池大小，最多有多少个线程同时并发
                                                                        .withCoreSize(2)
                                                                        // 排队，默认为 -1 ，假设 10 个请求，2 个执行，2 个排队，那么其他 6 个将直接返回错误
                                                                        .withMaxQueueSize(2)));
    this.productId = productId;
  }

  @Override
  protected ProductInfo run() throws Exception {
    // String url = "http://localhost:7000/getProduct?productId=" + productId;
    // String response = HttpUtils.get(url);
    log.info("睡眠 5 秒，模拟，productId: " + productId);
    TimeUnit.SECONDS.sleep(5);
    // Result result = JsonUtils.readValue(response, Result.class);
    // Result nonNull = Objects.requireNonNull(result, "无效的商品结果");
    // return (ProductInfo) nonNull.getData();
    return ProductInfo.getDefaultInstance(productId);
  }

  @Override
  protected String getCacheKey() {
    return String.valueOf(productId);
  }

  @Override
  protected ProductInfo getFallback() {
    log.warn("触发快速失败方法，productId: " + productId);
    return ProductInfo.getDefaultInstance(-1L);
  }
}
