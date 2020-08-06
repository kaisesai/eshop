package com.liukai.eshop.cache.ha.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * 批量处理结果失败，降级时按照进度恢复的命令
 */
public class BatchResultFailsFastCommand extends HystrixObservableCommand<Integer> {

  private int lastSeen = 0;

  public BatchResultFailsFastCommand() {
    super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
  }

  @Override
  protected Observable<Integer> construct() {
    // 创建可观察的类
    // 产生 1，2，3 个数值（模拟请求），在第 4 个请求时抛出一个异常
    Observable<Integer> observable = Observable.just(1, 2, 3);
    observable = observable.concatWith(Observable.error(new RuntimeException("forced error")));
    // 创建一个观察者，它的动作是将值赋给 lastSeen
    observable = observable.doOnNext(t1 -> lastSeen = t1);
    // 订阅在一个调度器实例上
    observable = observable.subscribeOn(Schedulers.computation());
    return observable;
  }

  @Override
  protected Observable<Integer> resumeWithFallback() {
    // 走降级机制，判断进度，接着进度返回
    if (lastSeen < 4) {
      return Observable.range(lastSeen + 1, 4 - lastSeen);
    } else {
      return Observable.empty();
    }
  }
}
