package com.liukai.eshop.cache.ha.command;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.AbstractConfiguration;

/**
 * 门面模式之主备系统命令
 */
@Slf4j
public class CommandFacadeWithPrimarySecondary extends HystrixCommand<String> {

  private final boolean usePromary;

  private final int id;

  public CommandFacadeWithPrimarySecondary(int id) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("PrimarySecondaryCommand"))
                .andCommandPropertiesDefaults(
                  // 这里使用信号量，因为至少包装其他两个 command，
                  // 其他两个 command 会使用线程池
                  // 信号量使用的场景是，内部代码逻辑不会涉及调用类似网络接口、io 之类的延迟较大的逻辑代码，一般是都调用耗时较短的逻辑
                  HystrixCommandProperties.Setter().withExecutionIsolationStrategy(
                    HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
    this.id = id;
    AbstractConfiguration configInstance = ConfigurationManager.getConfigInstance();
    System.out.println(configInstance);
    usePromary = configInstance.getBoolean("primarySecondary.usePrimary");
  }

  @Override
  protected String run() {
    log.info("============================= usePromary：" + usePromary);
    if (usePromary) {
      return new PrimaryCommand(id).execute();
    } else {
      return new SecondaryCommand(id).execute();
    }
  }

  private static class PrimaryCommand extends HystrixCommand<String> {

    private final int id;

    private PrimaryCommand(int id) {
      super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                  .andCommandKey(HystrixCommandKey.Factory.asKey("PrimaryCommand"))
                  .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("PrimaryCommand"))
                  .andCommandPropertiesDefaults(
                    // 设置为超时未 600 毫秒
                    HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(600)));
      this.id = id;
    }

    @Override
    protected String run() {
      // 执行主服务调用
      log.info("---------------  " + "responseFromPrimary-" + id);
      return "responseFromPrimary-" + id;
    }

  }

  private static class SecondaryCommand extends HystrixCommand<String> {

    private final int id;

    private SecondaryCommand(int id) {
      super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
                  .andCommandKey(HystrixCommandKey.Factory.asKey("SecondaryCommand"))
                  .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SecondaryCommand"))
                  .andCommandPropertiesDefaults(
                    // 设置超时为 100 毫秒
                    HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(100)));
      this.id = id;
    }

    @Override
    protected String run() {
      // 由于超时的设置，意味着备用服务将会更快的响应数据
      // 主备设置不同的超时时间，表达的意思是，他们调用响应数据的时间一个慢，一个快
      log.info("---------------  " + "responseFromSecondary-" + id);
      return "responseFromSecondary-" + id;
    }

  }
}
