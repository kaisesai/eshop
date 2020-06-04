package com.liukai.eshop.inventory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liukai.eshop.inventory.entity.User;
import com.liukai.eshop.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (User)表控制层
 *
 * @author liukai
 * @since 2020-06-03 19:27:43
 */
@RestController
@RequestMapping("user")
public class UserController {

  /**
   * 服务对象
   */
  @Autowired
  private UserService userService;

  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("selectOne")
  public User selectOne(@RequestParam Long id) {
    return this.userService.getById(id);
  }

  @GetMapping("selectOneFromCache")
  public User selectOneFromCache(@RequestParam Long id) throws JsonProcessingException {
    return this.userService.getCachedUserInfo(id);
  }

  @GetMapping("userList")
  public List<User> userList() {
    return this.userService.list();
  }

  @PostMapping("saveOrUpdate")
  public Boolean saveOrUpdateUser(@RequestBody User user) {
    return this.userService.saveOrUpdate(user);
  }

}
