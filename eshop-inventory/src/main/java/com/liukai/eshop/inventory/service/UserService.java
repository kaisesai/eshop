package com.liukai.eshop.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.liukai.eshop.model.entity.User;

/**
 * (User)表服务接口
 *
 * @author liukai
 * @since 2020-06-03 19:27:43
 */
public interface UserService extends IService<User> {

  User getCachedUserInfo(Long id) throws JsonProcessingException;

}
