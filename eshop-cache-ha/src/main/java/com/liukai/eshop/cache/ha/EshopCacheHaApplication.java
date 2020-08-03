package com.liukai.eshop.cache.ha;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.Filter;

@EnableTransactionManagement
@MapperScan(basePackages = "com.liukai.eshop.product.ha")
@SpringBootApplication(scanBasePackages = "com.liukai.eshop")
public class EshopCacheHaApplication {

  public static void main(String[] args) {
    SpringApplication.run(EshopCacheHaApplication.class, args);
  }

  /**
   * filter 过滤器
   *
   * @return
   */
  @Bean
  public FilterRegistrationBean<Filter> filterRegistrationBean() {
    FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
    // 在 jdk8 中 Filter 接口 除了 javax.servlet.Filter.doFilter 方法外，其他两个方法都是默认方法了
    filterFilterRegistrationBean.setFilter((request, response, chain) -> {
      HystrixRequestContext context = HystrixRequestContext.initializeContext();
      try {
        chain.doFilter(request, response);
      } finally {
        context.shutdown();
      }
    });

    filterFilterRegistrationBean.addUrlPatterns("/*");
    return filterFilterRegistrationBean;
  }
}
