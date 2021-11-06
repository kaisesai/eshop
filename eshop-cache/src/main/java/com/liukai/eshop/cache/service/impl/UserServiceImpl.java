package com.liukai.eshop.cache.service.impl;

import com.liukai.eshop.cache.model.dto.UserDTO;
import com.liukai.eshop.cache.service.UserService;
import com.liukai.eshop.model.entity.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
  
  private final AtomicLong idCounter = new AtomicLong();
  
  private final AtomicLong cacheLoadCounter = new AtomicLong();
  
  private final Map<Long, User> userMap = new ConcurrentHashMap<>();
  
  @Cacheable(cacheNames = "mycache", cacheManager = "caffeineCacheManager", key = "#id")
  @Override
  public User getByIdFromCache(Long id) {
    log.info("getByIdFromCache, id: {}", id);
    return this.getById(id);
  }
  
  @Cacheable(cacheNames = "mycache2", cacheManager = "caffeineCacheManager", key = "#id")
  @Override
  public User getByIdFromCache2(Long id) {
    log.info("getByIdFromCache2, id: {}", id);
    return this.getById(id);
  }
  
  @SneakyThrows
  @Override
  public User getById(Long id) {
    long counter = cacheLoadCounter.incrementAndGet();
    log.info("getById, id: {}", id);
    log.info("模拟 IO 读取数据，睡眠 2 秒...");
    TimeUnit.SECONDS.sleep(2);
    User user = userMap.getOrDefault(id, User.DEFAULT_INSTANCE);
    log.info("load data, id:{}, user:{}, counter:{} ", id, user, counter);
    return user;
  }
  
  @CacheEvict(cacheNames = "mycache", cacheManager = "caffeineCacheManager", key = "#id")
  @Override
  public Long deleteById(Long id) {
    log.info("deleteById, id: {}", id);
    userMap.remove(id);
    return id;
  }
  
  @CacheEvict(cacheNames = "mycache", cacheManager = "caffeineCacheManager", key = "#id")
  @Override
  public User update(UserDTO userDTO) {
    log.info("update user: {}", userDTO);
    return getNewUser(userDTO);
  }
  
  @CacheEvict(cacheNames = "mycache2", cacheManager = "caffeineCacheManager", key = "#id")
  @Override
  public User update2(UserDTO userDTO) {
    log.info("update2 user: {}", userDTO);
    return getNewUser(userDTO);
  }
  
  @CacheEvict(cacheNames = "mycache2", cacheManager = "caffeineCacheManager", key = "#id")
  @Override
  public User update3(UserDTO userDTO) {
    log.info("update3 user: {}", userDTO);
    return getNewUser(userDTO);
  }
  
  private User getNewUser(UserDTO userDTO) {
    User user = userMap.get(userDTO.getId());
    Validate.notNull(user);
    User newUser = userDTO.convertToUser();
    userMap.put(userDTO.getId(), newUser);
    return newUser;
  }
  
  @CachePut(cacheNames = "mycache", cacheManager = "caffeineCacheManager", key = "#userDTO.id")
  @Override
  public User save(UserDTO userDTO) {
    log.info("save user: {}", userDTO);
    long id = idCounter.incrementAndGet();
    userDTO.setId(id);
    User newUser = userDTO.convertToUser();
    userMap.put(userDTO.getId(), newUser);
    return newUser;
  }
  
}
