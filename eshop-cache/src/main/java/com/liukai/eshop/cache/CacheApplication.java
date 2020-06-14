package com.liukai.eshop.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@EnableCaching
@SpringBootApplication(scanBasePackages = "com.liukai.eshop")
public class CacheApplication {

  public static void main(String[] args) {
    SpringApplication.run(CacheApplication.class, args);
  }

}
