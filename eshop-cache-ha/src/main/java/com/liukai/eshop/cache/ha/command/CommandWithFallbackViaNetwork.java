package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 多级降级机制的 command
 * <p>
 * 当远程网络请求失败时，执行另外一个 command
 */
@Slf4j
public class CommandWithFallbackViaNetwork extends HystrixCommand<String> {

  private final int id;

  public CommandWithFallbackViaNetwork(int id) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueCommand")));
    this.id = id;
  }

  @Override
  protected String run() throws Exception {
    throw new RuntimeException("force failure for example");
  }

  @Override
  protected String getFallback() {
    // 降级机制，执行另一个 command
    log.info("执行失败，准备执行一级降级");
    return new FallbackViaNetwork(id).execute();
  }

  private static class FallbackViaNetwork extends HystrixCommand<String> {

    private final int id;

    public FallbackViaNetwork(int id) {
      super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceX"))
                  .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueFallbackCommand"))
                  // 注意这里：需要使用和正常 command 不一样的线程池
                  // 因为正常 comman 执行降级的话有可能是因为线程池满了导致的
                  .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("RemoteServiceXFallback")));
      this.id = id;
    }

    @Override
    protected String run() {
      // 第一级降级策略：通过网络获取数据
      // MemCacheClient.getValue(id);
      // log.info("开始执行一级降级");
      throw new RuntimeException("一级降级失败了 the fallback also failed");
    }

    @Override
    protected String getFallback() {
      // 第二级降级策略：可以使用 stubbed fallback 方案返回残缺的数据
      // 也可以返回一个 null
      log.info("执行二级降级，返回 null");
      return null;
    }
  }

}
