package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;

/**
 * stubbed 快速失败的观察者命令
 */
public class ObservableCommandWithStubbedFallback
  extends HystrixObservableCommand<CommandWithStubbedFallback.UserAccount> {

  private final int customerId;

  private final String countryCodeFromGeoLookup;

  public ObservableCommandWithStubbedFallback(int customerId, String countryCodeFromGeoLookup) {
    super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
    this.customerId = customerId;
    this.countryCodeFromGeoLookup = countryCodeFromGeoLookup;
  }

  @Override
  protected Observable<CommandWithStubbedFallback.UserAccount> construct() {
    // fetch UserAccount from remote service
    //        return UserAccountClient.getAccount(customerId);
    throw new RuntimeException("forcing failure for example");
  }

  @Override
  protected Observable<CommandWithStubbedFallback.UserAccount> resumeWithFallback() {
    return Observable.just(new CommandWithStubbedFallback.UserAccount(customerId, "Unknow Name",
                                                                      countryCodeFromGeoLookup,
                                                                      true, true, true));
  }
}
