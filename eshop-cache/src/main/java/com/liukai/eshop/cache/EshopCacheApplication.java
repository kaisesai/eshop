package com.liukai.eshop.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.liukai.eshop",
                       exclude = DataSourceAutoConfiguration.class)
public class EshopCacheApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(EshopCacheApplication.class, args);
  }
  
}
