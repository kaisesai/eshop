package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandCircuit extends HystrixCommand<String> {

  private final boolean throwException;

  public CommandCircuit(boolean throwException) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandCircuit"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                      // 10 秒时间窗口流量达到 10 个；默认是 20
                                                                      .withCircuitBreakerRequestVolumeThreshold(
                                                                        10)
                                                                      // 当异常占比超过 50% ；默认值是 50
                                                                      .withCircuitBreakerErrorThresholdPercentage(
                                                                        60)
                                                                      // 断路器打开之后，后续请求都会被拒绝并走降级机制，打开 3 秒后，变成半开状态
                                                                      .withCircuitBreakerSleepWindowInMilliseconds(
                                                                        3000)));
    this.throwException = throwException;
  }

  @Override
  protected String run() throws Exception {
    if (throwException) {
      throw new RuntimeException("failure from CommandThatFailsFast");
    } else {
      return "success";
    }
  }

  @Override
  protected String getFallback() {
    return "降级机制";
  }

}
