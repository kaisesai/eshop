package com.liukai.eshop.cache.ha.collapser;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 命令请求合并——根据 key 获取 value
 */
@Slf4j
public class CommandCollapserGetValueForKey
  extends HystrixCollapser<List<String>, String, Integer> {

  private final Integer key;

  public CommandCollapserGetValueForKey(Integer key) {
    this.key = key;
  }

  /**
   * @return 单个 command 的请求参数
   */
  @Override
  public Integer getRequestArgument() {
    return key;
  }

  /**
   * 聚合多个命令由框架完成，这里只需要创建我们的 batchCommand 即可
   *
   * @param collapsedRequests 多个请求的参数列表
   * @return
   */
  @Override
  protected HystrixCommand<List<String>> createCommand(
    Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
    return new BatchCommand(collapsedRequests);
  }

  /**
   * 将返回的数据对请求进行映射，外部的单个请求才能获取到对应的结果
   *
   * @param batchResponse
   * @param collapsedRequests
   */
  @Override
  protected void mapResponseToRequests(List<String> batchResponse,
                                       Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
    int count = 0;
    for (CollapsedRequest<String, Integer> collapsedRequest : collapsedRequests) {
      // 将请求的结果再分发到对应的请求中去
      collapsedRequest.setResponse(batchResponse.get(count++));
    }
  }

  // @Slf4j
  private static final class BatchCommand extends HystrixCommand<List<String>> {

    private final Collection<CollapsedRequest<String, Integer>> requests;

    public BatchCommand(Collection<CollapsedRequest<String, Integer>> requests) {
      super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                  .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKy")));
      this.requests = requests;
    }

    @Override
    protected List<String> run() throws Exception {
      List<String> results = new ArrayList<>();
      // 模拟一个接口获取批量数据
      for (CollapsedRequest<String, Integer> request : requests) {
        results.add("ValueForKey: " + request.getArgument());
      }
      log.info("请求合并-BatchCommand 执行");
      return results;
    }
  }

}
