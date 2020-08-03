package com.liukai.eshop.cache.ha.collapser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.liukai.eshop.common.util.HttpUtils;
import com.liukai.eshop.common.util.JsonUtils;
import com.liukai.eshop.model.entity.ProductInfo;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CollapserGetProductCommand
  extends HystrixCollapser<List<ProductInfo>, ProductInfo, Long> {

  private final Long productId;

  public CollapserGetProductCommand(Long productId) {
    super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("CollapserGetProductCommand"))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter()
                                                                          // 在 TimerDelayInMilliseconds 内最多允许多少个 request 被合并
                                                                          // 默认是无限大，该参数一般不使用，而是使用时间来触发合并请求提交
                                                                          .withMaxRequestsInBatch(
                                                                            10)
                                                                          // 时间窗口：合并请求需要等待多久
                                                                          // 默认是 10ms ，
                                                                          .withTimerDelayInMilliseconds(
                                                                            20)));
    this.productId = productId;
  }

  @Override
  public Long getRequestArgument() {
    return productId;
  }

  @Override
  protected HystrixCommand<List<ProductInfo>> createCommand(
    Collection<CollapsedRequest<ProductInfo, Long>> collapsedRequests) {
    return new BatchCommand(collapsedRequests);
  }

  @Override
  protected void mapResponseToRequests(List<ProductInfo> batchResponse,
                                       Collection<CollapsedRequest<ProductInfo, Long>> collapsedRequests) {
    int count = 0;
    for (CollapsedRequest<ProductInfo, Long> collapsedRequest : collapsedRequests) {
      collapsedRequest.setResponse(batchResponse.get(count++));
    }
    log.info("映射数量：" + collapsedRequests.size());
  }

  private static class BatchCommand extends HystrixCommand<List<ProductInfo>> {

    private final Collection<CollapsedRequest<ProductInfo, Long>> collapsedRequests;

    public BatchCommand(Collection<CollapsedRequest<ProductInfo, Long>> collapsedRequests) {
      super((Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                   .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKy"))));
      this.collapsedRequests = collapsedRequests;
      log.info("此次请求大小：" + collapsedRequests.size());
    }

    @Override
    protected List<ProductInfo> run() throws Exception {
      // 从当前合并的多个请求中，按顺序拼接请求 productId
      String productIdsStr = collapsedRequests.stream().map(CollapsedRequest::getArgument)
                                              .map(String::valueOf)
                                              .collect(Collectors.joining(","));
      log.info("批量获取接口请求：" + productIdsStr);
      String url = "http://localhost:7000/product/ha/getProducts?productIdsStr=" + productIdsStr;
      String response = HttpUtils.get(url);
      List<ProductInfo> productInfos = Collections.emptyList();
      try {
        productInfos = JsonUtils.readValue(response, new TypeReference<List<ProductInfo>>() {
        });
      } catch (Exception e) {
        log.error("parse productInfos fail", e);
      }
      return productInfos;
    }
  }
}
