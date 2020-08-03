package com.liukai.eshop.product.ha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.liukai.eshop")
public class EshopProductHaApplication {

  public static void main(String[] args) {
    SpringApplication.run(EshopProductHaApplication.class, args);
  }

}
