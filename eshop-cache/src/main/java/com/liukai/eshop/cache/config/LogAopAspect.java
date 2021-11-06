package com.liukai.eshop.cache.config;

import com.liukai.eshop.common.util.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志 aop 切面
 */
@Slf4j
@Aspect
@Component
public class LogAopAspect {
  
  /**
   * 切点
   */
  @Pointcut(value = "execution(public * com.liukai.eshop.cache.controller..*.*(..))")
  public void point() {
  }
  
  @SneakyThrows
  @Around(value = "point()")
  public Object around(ProceedingJoinPoint joinPoint) {
    // 目标方法名称
    String signature = joinPoint.getSignature().toLongString();
    String basicMsg = "执行方法" + signature;
    StopWatch stopWatch = StopWatch.createStarted();
    try {
      Object[] args = joinPoint.getArgs();
      log.info(basicMsg + "开始，请求参数：{}", JsonUtils.writeValueAsString(args));
      Object result = joinPoint.proceed(args);
      log.info(basicMsg + "结束，返回结果：{}", JsonUtils.writeValueAsString(result));
      return result;
    } catch (Throwable e) {
      log.error(basicMsg + "异常", e);
      throw e;
    } finally {
      stopWatch.stop();
      log.info(basicMsg + "耗时：{}ms", stopWatch.getTime());
    }
  }
  
}
