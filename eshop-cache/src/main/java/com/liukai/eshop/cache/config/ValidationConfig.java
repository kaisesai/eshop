package com.liukai.eshop.cache.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ValidationConfig {
  
  @Bean
  public LocalValidatorFactoryBean validator(MessageSource messageSource) {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource);
    return bean;
  }
  
  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource
      = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:i18n");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
  /**
   * @return 本地验证器工厂 bean
   */
  // @Bean
  // public LocalValidatorFactoryBean validator() {
  //   return new LocalValidatorFactoryBean();
  // }
  
  /**
   * @return 方法验证后置处理器
   */
  @Bean
  public MethodValidationPostProcessor validationPostProcessor() {
    return new MethodValidationPostProcessor();
  }
  
}
