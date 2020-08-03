package com.liukai.eshop.inventory.request;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 请求队列
 * <p>
 * 通过 applicationRunner 机制，在系统初始化时候，对线程池进行初始化操作
 */
@Component
public class RequestQueue implements ApplicationRunner {

  private final List<ArrayBlockingQueue<Request>> queues = new ArrayList<>();

  @Override
  public void run(ApplicationArguments args) throws Exception {
    int workThread = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(workThread);

    // 创建10 个后台线程，每个线程维护一个阻塞队列
    for (int i = 0; i < workThread; i++) {
      // 阻塞队列中最多可以放 100 对象
      ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<>(100);

      // 请求处理任务，主要负责从队列中获取请求以执行
      Callable<Boolean> callable = new RequestProcessorTask(queue);
      executorService.submit(callable);

      // 维护队列
      queues.add(queue);
    }
  }

  public ArrayBlockingQueue<Request> getQueue(int index) {
    return queues.get(index);
  }

  public int queueSize(){
    return queues.size();
  }

}
