package com.liukai.cache.ha.test;

import com.liukai.eshop.cache.ha.collapser.CommandCollapserGetValueForKey;
import com.liukai.eshop.cache.ha.command.*;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class CommandTest {

  private final Logger log = LoggerFactory.getLogger(CommandTest.class);

  @Test
  public void test4() throws InterruptedException, ExecutionException {
    // 线程池中最大允许的任务数量 = 线程池最大线程数 + 队列等待数量
    BlockingQueue queue;
    queue = new SynchronousQueue(); // 1
    // queue = new LinkedBlockingDeque<>(20);  // 2
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
                                                                   queue);

    int threadNum = 71;
    CountDownLatch c = new CountDownLatch(threadNum);
    IntStream.range(0, threadNum).parallel().mapToObj(item -> (Runnable) () -> {
      System.out.println(Thread.currentThread().getName());
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      c.countDown();

    }).forEach(threadPoolExecutor::submit);
    c.await();
  }

  @Test
  public void testPrimary() {
    HystrixRequestContext context = HystrixRequestContext.initializeContext();
    try {
      ConfigurationManager.getConfigInstance().setProperty("primarySecondary.usePrimary", true);
      Assert.assertEquals("responseFromPrimary-20",
                          new CommandFacadeWithPrimarySecondary(20).execute());
      // 切换为使用备用
      ConfigurationManager.getConfigInstance().setProperty("primarySecondary.usePrimary", false);
      Assert.assertEquals("responseFromSecondary-20",
                          new CommandFacadeWithPrimarySecondary(20).execute());
    } finally {
      context.shutdown();
      ConfigurationManager.getConfigInstance().clear();
    }
  }

  @Test
  public void testSecondary() {
    HystrixRequestContext context = HystrixRequestContext.initializeContext();
    try {
      ConfigurationManager.getConfigInstance().setProperty("primarySecondary.usePrimary", false);
      Assert.assertEquals("responseFromSecondary-20",
                          new CommandFacadeWithPrimarySecondary(20).execute());
    } finally {
      context.shutdown();
      ConfigurationManager.getConfigInstance().clear();
    }
  }

  @Test
  public void testGetProductMultipleFallbackCommand() {
    try (HystrixRequestContext ignored = HystrixRequestContext.initializeContext()) {
      GetProductMultipleFallbackCommand command = new GetProductMultipleFallbackCommand(2L);
      command.execute();
    }
  }

  @Test
  public void testMultiFallback() {
    CommandWithFallbackViaNetwork command = new CommandWithFallbackViaNetwork(1);
    String result = command.execute();
    log.info("执行结果：{}", result);
  }

  @Test
  public void testBatchResultFailsFastCommand() {
    BatchResultFailsFastCommand command = new BatchResultFailsFastCommand();
    Observable<Integer> observe = command.observe();
    Iterator<Integer> iterator = observe.toBlocking().getIterator();
    while (iterator.hasNext()) {
      System.out.println("BatchResultFailsFastCommand on consumer: " + iterator.next());
    }
  }

  @Test
  public void testObserverCommondStubbedFallback() {

    ObservableCommandWithStubbedFallback command = new ObservableCommandWithStubbedFallback(1,
                                                                                            "CN");

    // 懒汉式
    Observable<CommandWithStubbedFallback.UserAccount> userAccountObservable = command
      .toObservable();

    userAccountObservable.subscribe(userAccount -> {
      System.out.println("懒汉式观察者，监听到对象：" + userAccount);
    });

    // // 饿汉式
    // Observable<CommandWithStubbedFallback.UserAccount> observe = command.observe();
    // observe.subscribe(userAccount -> {
    //   System.out.println("饿汉式观察者，监听到对象：" + userAccount);
    // });

  }

  @Test
  public void testStubbedFallback() {
    CommandWithStubbedFallback command = new CommandWithStubbedFallback(1, "CN");
    CommandWithStubbedFallback.UserAccount execute = command.execute();
    System.out.println(execute);
  }

  @Test
  public void testCollapser() throws ExecutionException, InterruptedException {
    // 创建一个上下文
    try (HystrixRequestContext ignored = HystrixRequestContext.initializeContext()) {
      Future<String> f1 = new CommandCollapserGetValueForKey(1).queue();
      Future<String> f2 = new CommandCollapserGetValueForKey(2).queue();
      Future<String> f3 = new CommandCollapserGetValueForKey(3).queue();
      Future<String> f4 = new CommandCollapserGetValueForKey(4).queue();
      Future<String> f5 = new CommandCollapserGetValueForKey(5).queue();

      log.info(f1.get());
      log.info(f2.get());
      log.info(f3.get());
      log.info(f4.get());
      log.info(f5.get());

      HystrixRequestLog currentRequest = HystrixRequestLog.getCurrentRequest();
      Collection<HystrixInvokableInfo<?>> allExecutedCommands = currentRequest
        .getAllExecutedCommands();
      log.info("当前线程请求实际执行命令次数：" + allExecutedCommands.size());
      HystrixInvokableInfo<?>[] hystrixInvokableInfos = allExecutedCommands
        .toArray(new HystrixInvokableInfo<?>[0]);
      HystrixInvokableInfo<?> invokableInfo = hystrixInvokableInfos[0];
      log.info("其中一个 command 的名称：" + invokableInfo.getCommandKey());
      log.info("command 执行的事件：" + invokableInfo.getExecutionEvents());

    }
  }

  @Test
  public void testLimit() throws InterruptedException {
    int count = 13;
    CountDownLatch countDownLatch = new CountDownLatch(count);
    for (int i = 0; i < count; i++) {
      new Thread(() -> {
        CommandLimit commandLimit = new CommandLimit();
        String execute = commandLimit.execute();
        log.info(Thread.currentThread().getName() + " 线程 execute：" + execute + " " + new Date());
        countDownLatch.countDown();
      }, "Thread " + i).start();
    }
    countDownLatch.await();

  }

  @Test
  public void testCircuit() throws InterruptedException {

    for (int i = 0; i < 100; i++) {
      CommandCircuit commandCircuit = new CommandCircuit(i % 2 == 0);
      log.info(i + " - 执行结果：" + commandCircuit.execute());
    }
    // 请求数量 50% 的都报错了，此时断路器开启
    TimeUnit.SECONDS.sleep(1);
    // 这里休眠了 3 秒，并且再执行 3 次请求，那么，都会被降级处理，
    log.info("休眠 1 秒，执行请求");
    for (int i = 0; i < 1; i++) {
      // 注意了，这里需要休眠一秒之后，再次调用请求，目的是为了更新它的访问记录 hystrixCircuitBreaker 里的 circuitOpenedOrLastTestedTime 最近一次访问时间
      // 它是用来判断，根据它与当滑动窗口时间判断是否大于当前时间，来决定是否允许单一请求通过。
      // 根据   HealthCounts health = metrics.getHealthCounts(); 来决定记录滑动的执行次数
      CommandCircuit circuit = new CommandCircuit(false);
      log.info(circuit.execute());
    }

    // 这里再休眠 3 秒，执行一次请求，
    TimeUnit.SECONDS.sleep(3);
    log.info("3 秒钟之后，断路器变成半开闭状态");
    CommandCircuit commandCircuit = new CommandCircuit(false);
    log.info("尝试执行一次请求, execute = " + commandCircuit.execute());
    log.info("执行成功，断路器关闭，继续尝试访问");
    for (int i = 0; i < 3; i++) {
      CommandCircuit circuit = new CommandCircuit(false);
      log.info(circuit.execute());
    }
  }

  @Test
  public void testFailure() {
    String execute = new CommandThatFailsFast(true).execute();
    System.out.println(execute);
  }

  @Test
  public void testWithoutCacheHits() {
    HystrixRequestContext context = HystrixRequestContext.initializeContext();
    try {
      Assert.assertTrue(new CommandUsingRequestCache(2).execute());
      Assert.assertTrue(new CommandUsingRequestCache(2).execute());
      Assert.assertFalse(new CommandUsingRequestCache(1).execute());
      Assert.assertTrue(new CommandUsingRequestCache(2).execute());
      Assert.assertTrue(new CommandUsingRequestCache(0).execute());
      Assert.assertTrue(new CommandUsingRequestCache(58672).execute());
    } finally {
      context.shutdown();
    }
  }

  @Test
  public void testWithCacheHits() {
    HystrixRequestContext context = HystrixRequestContext.initializeContext();
    try {
      CommandUsingRequestCache command2a = new CommandUsingRequestCache(2);
      CommandUsingRequestCache command2b = new CommandUsingRequestCache(2);

      log.info("0");
      Assert.assertTrue(command2a.execute());
      // 第一次执行结果，所以不应该来自缓存
      Assert.assertFalse(command2a.isResponseFromCache());

      log.info("1");
      Assert.assertTrue(command2b.execute());
      // 这是第二次执行结果，应该来自缓存
      Assert.assertTrue(command2b.isResponseFromCache());
    } finally {
      // 关闭上下文
      context.shutdown();
    }

    // 开始一个新的请求上下文
    context = HystrixRequestContext.initializeContext();
    try {
      CommandUsingRequestCache command3b = new CommandUsingRequestCache(2);
      log.info("2");
      Assert.assertTrue(command3b.execute());
      // 当前的 command 是一个新的请求上下文
      // 所以也不应该来自缓存
      Assert.assertFalse(command3b.isResponseFromCache());
    } finally {
      context.shutdown();
    }
  }

}
