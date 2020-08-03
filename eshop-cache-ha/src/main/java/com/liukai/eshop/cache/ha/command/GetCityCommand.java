package com.liukai.eshop.cache.ha.command;

import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class GetCityCommand extends HystrixCommand<ProductInfo> {

  private final Long productId;

  public GetCityCommand(Long productId) {

    super(Setter
            // 命令组
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductCommandGroup"))
            // 命令 key
            .andCommandKey(HystrixCommandKey.Factory.asKey("GetCityCommand"))
            // 线程池 key
            // .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetCityThreadPool"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                  // 设置 4 秒超时，看是否有效果
                                                                  .withExecutionTimeoutInMilliseconds(
                                                                    4000)
                                                                  // 隔离策略为信号量
                                                                  .withExecutionIsolationStrategy(
                                                                    HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                                                  // 信号量最大请求数量设置
                                                                  .withExecutionIsolationSemaphoreMaxConcurrentRequests(
                                                                    2))

         );

    this.productId = productId;
  }

  @Override
  protected ProductInfo run() throws Exception {
    log.info(Thread.currentThread().getName());
    log.info("睡眠 5 秒，模拟");
    TimeUnit.SECONDS.sleep(5);
    return ProductInfo.getDefaultInstance(productId);
  }
}
