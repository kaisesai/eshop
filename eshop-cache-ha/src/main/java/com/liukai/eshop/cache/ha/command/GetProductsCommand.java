package com.liukai.eshop.cache.ha.command;

import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.List;

@Slf4j
public class GetProductsCommand extends HystrixObservableCommand<ProductInfo> {

  private final List<Long> pids;

  public GetProductsCommand(List<Long> pids) {
    super(HystrixCommandGroupKey.Factory.asKey("GetProductCommandGroup"));
    this.pids = pids;
  }

  @Override
  protected Observable<ProductInfo> construct() {
    return Observable.unsafeCreate((Observable.OnSubscribe<ProductInfo>) observer -> {
      try {
        // 没有取消订阅
        if (!observer.isUnsubscribed()) {
          for (Long pid : pids) {
            ProductInfo productInfo = ProductInfo.getDefaultInstance(pid);
            // a real example would do work like a network call here
            observer.onNext(productInfo);
          }
          observer.onCompleted();
        }
      } catch (Exception e) {
        observer.onError(e);
      }
    }).subscribeOn(Schedulers.io());

  }
}
