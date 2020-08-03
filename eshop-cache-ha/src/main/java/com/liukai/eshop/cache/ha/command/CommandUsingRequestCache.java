package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandUsingRequestCache extends HystrixCommand<Boolean> {

  private final int value;

  public CommandUsingRequestCache(int value) {
    super(HystrixCommandGroupKey.Factory.asKey("CommandUsingRequestCacheGroup"));
    this.value = value;
  }

  @Override
  protected Boolean run() throws Exception {
    // 当值为 0 或者是 2 的整倍数的时候，返回 true
    log.info("run 方法被执行");
    return value == 0 || value % 2 == 0;
  }

  @Override
  protected String getCacheKey() {
    return String.valueOf(value);
  }
}
