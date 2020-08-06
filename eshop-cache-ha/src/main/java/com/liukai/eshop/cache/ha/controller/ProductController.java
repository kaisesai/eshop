package com.liukai.eshop.cache.ha.controller;

import com.liukai.eshop.cache.ha.collapser.CollapserGetProductCommand;
import com.liukai.eshop.cache.ha.command.GetCityCommand;
import com.liukai.eshop.cache.ha.command.GetProductCommand;
import com.liukai.eshop.cache.ha.command.GetProductMultipleFallbackCommand;
import com.liukai.eshop.cache.ha.command.GetProductsCommand;
import com.liukai.eshop.model.entity.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping(value = "/product")
@RestController
public class ProductController {

  @GetMapping(value = "/getformultiplefallback")
  public ProductInfo getForMultipleFallback(@RequestParam(value = "product_id") Long productId) {
    GetProductMultipleFallbackCommand command = new GetProductMultipleFallbackCommand(productId);
    return command.execute();
  }

  @GetMapping(value = "/get")
  public ProductInfo getProductInfo(@RequestParam(value = "product_id") Long productId) {
    GetProductCommand getProductCommand = new GetProductCommand(productId);
    return getProductCommand.execute();
  }

  @PostMapping(value = "/getProductsForCache")
  public List<ProductInfo> getProductInfosForCache(@RequestBody List<Long> pids) {
    return pids.stream().map(pid -> {
      GetProductCommand getProductCommand = new GetProductCommand(pid);
      ProductInfo productInfo = getProductCommand.execute();
      log.info("pid: " + pid + ", 是否来自于缓存：" + getProductCommand.isResponseFromCache());
      return productInfo;
    }).collect(Collectors.toList());
  }

  @PostMapping(value = "/getProducts")
  public void getProductInfos(@RequestBody List<Long> pids) {
    GetProductsCommand getProductsCommand = new GetProductsCommand(pids);
    // List<ProductInfo> productInfos = new ArrayList<>();
    Observable<ProductInfo> observe = getProductsCommand.observe();

    // 第一种方式调用：Action1 lambda 表达式，订阅获取每一条结果
    observe.subscribe(productInfo -> {
      log.info("Action " + productInfo.toString());
      // productInfos.add(productInfo);
    });
    System.out.println("方法执行完毕");

    // 第二种方式：观察者对象
    observe.subscribe(new Observer<ProductInfo>() {
      @Override
      public void onCompleted() {
        log.info("Observer onCompleted.");
      }

      @Override
      public void onError(Throwable e) {
        log.error("Observer error", e);
      }

      @Override
      public void onNext(ProductInfo productInfo) {
        log.info("Observer onNext, " + productInfo.toString());

      }
    });

    // 第三种方式：订阅者模式
    observe.subscribe(new Subscriber<ProductInfo>() {
      @Override
      public void onCompleted() {
        log.info("Subscriber onCompleted.");
      }

      @Override
      public void onError(Throwable e) {
        log.error("Subscriber error", e);
      }

      @Override
      public void onNext(ProductInfo productInfo) {
        log.info("Subscriber onNext, " + productInfo.toString());
      }
    });

    // 同步调用
    observe.toBlocking().forEach(productInfo -> {
      log.info("toBlocking , " + productInfo.toString());
    });
    // return productInfos;
  }

  @RequestMapping("/semaphore/getProduct")
  public ProductInfo semaphoreGetProduct(@RequestParam(value = "product_id") Long productId) {
    GetCityCommand getCityCommand = new GetCityCommand(productId);
    System.out.println(Thread.currentThread().getName());
    ProductInfo productInfo = getCityCommand.execute();
    return productInfo;
  }

  @PostMapping(value = "/getProductsforcollapser")
  public List<ProductInfo> getProductInfosForCollapser(@RequestBody List<Long> pids) {

    List<Future<ProductInfo>> futures = pids.stream().map(pid -> {
      CollapserGetProductCommand command = new CollapserGetProductCommand(pid);
      // 异步调用
      return command.queue();
    }).collect(Collectors.toList());

    return futures.stream().map(productInfoFuture -> {
      ProductInfo productInfo = null;
      try {
        productInfo = productInfoFuture.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
      log.info("请求结果：" + productInfo);
      return productInfo;
    }).collect(Collectors.toList());

  }

}
