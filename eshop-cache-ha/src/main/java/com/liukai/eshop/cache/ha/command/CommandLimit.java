package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CommandLimit extends HystrixCommand<String> {

  public CommandLimit() {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandLimitGroup"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                                                            // 配置线程池大小，同时并发能力个数
                                                                            .withCoreSize(2)
                                                                            // 配置等待线程个数；如果不配置该项，则没有等待，超过则拒绝
                                                                            .withMaxQueueSize(5)
                                                                            // 由于 maxQueueSize 是初始化固定的，该配置项是动态调整最大等待数量的
                                                                            // 可以热更新；规则：只能比 MaxQueueSize 小，
                                                                            .withQueueSizeRejectionThreshold(
                                                                              2))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                      // 修改为 2 秒超时
                                                                      .withExecutionTimeoutInMilliseconds(
                                                                        2000))

         );
  }

  @Override
  protected String run() throws Exception {
    /*
    特别注意：withQueueSizeRejectionThreshold 是热更新 withMaxQueueSize 配置的； 在该测试中，休眠和超时很重要，因为：
      - 休眠少了，那么执行速度过快，输出日志可能大于 withCoreSize + withQueueSizeRejectionThreshold 数量；
      - 休眠多了，那么排队中被释放出来的时候发现已经超时就走降级机制了，而不是还去请求；
     */
    TimeUnit.MILLISECONDS.sleep(800);
    return "success";
  }

  @Override
  protected String getFallback() {
    return "降级";
  }
}
