package com.liukai.eshop.inventory.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.liukai.eshop.inventory.entity.User;
import com.liukai.eshop.inventory.mapper.UserMapper;
import com.liukai.eshop.inventory.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * (User)表服务实现类
 *
 * @author liukai
 * @since 2020-06-03 19:27:43
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  public static final String REDIS_CACHE_PREFIX_KEY_USER = "cached_user_";

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Override
  public User getCachedUserInfo(Long id) throws JsonProcessingException {
    String userCacheKey = loadUserCacheKey(id);
    String userStr = stringRedisTemplate.opsForValue().get(userCacheKey);
    log.info("read user cache: {}", userStr);
    if (StringUtils.hasText(userStr)) {
      return JSON.parseObject(userStr, User.class);
    }
    // 查数据库
    User user = super.getById(id);
    if (user != null) {
      // 写入缓存
      userStr = JSON.toJSONString(user);
      stringRedisTemplate.opsForValue().set(userCacheKey, userStr);
      log.info("write user cache: {}", userStr);
    }
    return user;
  }

  private String loadUserCacheKey(Object key) {
    return REDIS_CACHE_PREFIX_KEY_USER + key.toString();
  }

}
