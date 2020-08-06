package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.Data;

/**
 * stubbed 快速失败的观察者命令
 */
public class CommandWithStubbedFallback
  extends HystrixCommand<CommandWithStubbedFallback.UserAccount> {

  private final int customerId;

  private final String countryCodeFromGeoLookup;

  /**
   * @param customerId               The customerID to retrieve UserAccount for
   * @param countryCodeFromGeoLookup The default country code from the HTTP request geo code lookup used for fallback.
   */
  public CommandWithStubbedFallback(int customerId, String countryCodeFromGeoLookup) {
    super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
    this.customerId = customerId;
    this.countryCodeFromGeoLookup = countryCodeFromGeoLookup;
  }

  @Override
  protected CommandWithStubbedFallback.UserAccount run() throws Exception {
    // fetch UserAccount from remote service
    //        return UserAccountClient.getAccount(customerId);
    throw new RuntimeException("forcing failure for example");
  }

  @Override
  protected UserAccount getFallback() {
    // 返回一些带过来的请求参数
    // 和一些默认值
    // 注意：不要在这里去远程请求，否则有可能出现这里请求又失败的问题
    return new UserAccount(customerId, "Unknown Name", countryCodeFromGeoLookup, true, true, false);
  }

  @Data
  public static class UserAccount {

    private final int customerId;

    private final String name;

    private final String countryCode;

    private final boolean isFeatureXPermitted;

    private final boolean isFeatureYPermitted;

    private final boolean isFeatureZPermitted;

    UserAccount(int customerId, String name, String countryCode, boolean isFeatureXPermitted,
                boolean isFeatureYPermitted, boolean isFeatureZPermitted) {
      this.customerId = customerId;
      this.name = name;
      this.countryCode = countryCode;
      this.isFeatureXPermitted = isFeatureXPermitted;
      this.isFeatureYPermitted = isFeatureYPermitted;
      this.isFeatureZPermitted = isFeatureZPermitted;
    }
  }
}
