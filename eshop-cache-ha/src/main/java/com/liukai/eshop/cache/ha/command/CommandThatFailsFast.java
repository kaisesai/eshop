package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandThatFailsFast extends HystrixCommand<String> {

  private final boolean throwException;

  public CommandThatFailsFast(boolean throwException) {
    super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
    this.throwException = throwException;
  }

  @Override
  protected String run() throws Exception {
    if (throwException) {
      throw new IllegalStateException("非法的状态异常 from CommandThatFailsFast");
    }
    log.info("执行成功 CommandThatFailsFast");
    return "success";
  }

  @Override
  protected String getFallback() {
    log.info("触发降级机制 CommandThatFailsFast");
    return "降级机制";
  }
}
