package com.liukai.eshop.cache.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.liukai.eshop.cache.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class MyCacheConfig {
  
  @Autowired
  private UserService userService;
  
  public Object loadUser(Object key) {
    log.warn("重新加载 user，key: {}", key);
    return userService.getById((Long) key);
  }
  
  @Bean(value = "caffeineCacheManager")
  public CacheManager caffeineCacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    
    // 注册自定义缓存加载器
    LoadingCache<Object, Object> myCache = buildCache();
    caffeineCacheManager.registerCustomCache("mycache", myCache);
    
    LoadingCache<Object, Object> myCache2 = buildCache();
    caffeineCacheManager.registerCustomCache("mycache2", myCache2);
    return caffeineCacheManager;
  }
  
  private LoadingCache<Object, Object> buildCache() {
    return Caffeine.newBuilder().maximumSize(10).expireAfterWrite(1, TimeUnit.MINUTES)
      .refreshAfterWrite(10, TimeUnit.SECONDS).build(new CacheLoader<>() {
        @Override
        public @Nullable Object load(@NonNull Object key) {
          return loadUser(key);
        }
      });
  }
  
}
