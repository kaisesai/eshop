package com.liukai.eshop.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liukai.eshop.inventory.entity.User;
import com.liukai.eshop.inventory.mapper.UserMapper;
import com.liukai.eshop.inventory.service.UserService;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author liukai
 * @since 2020-06-03 19:27:43
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
