package com.liukai.eshop.cache.controller;

import com.liukai.eshop.cache.config.group.Insert;
import com.liukai.eshop.cache.config.group.Update;
import com.liukai.eshop.cache.model.dto.UserDTO;
import com.liukai.eshop.cache.service.UserService;
import com.liukai.eshop.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RequestMapping(value = "/user")
@RestController
public class UserController {
  
  @Autowired
  private UserService userService;
  
  @GetMapping(value = "get")
  public User getUserById(
    @NotNull(message = "message.notnull.id") @RequestParam(value = "id") Long id) {
    return userService.getByIdFromCache(id);
  }
  
  @GetMapping(value = "get2")
  public User getUserById2(
    @Validated @Min(message = "{message.min.zero.id}", value = 1) @RequestParam(value = "id")
      Long id) {
    return userService.getByIdFromCache2(id);
  }
  
  @PostMapping(value = "/save")
  public User saveUser(@Validated(value = {Insert.class}) @RequestBody UserDTO userDTO) {
    return userService.save(userDTO);
  }
  
  @PutMapping(value = "/update")
  public User updateUser(@Validated(value = {Update.class}) @RequestBody UserDTO userDTO) {
    return userService.update(userDTO);
  }
  
  @PutMapping(value = "/update2")
  public User updateUser2(@Validated(value = {Update.class}) @RequestBody UserDTO userDTO) {
    return userService.update2(userDTO);
  }
  
  @PutMapping(value = "/update3")
  public User updateUser3(@Validated(value = {Update.class}) @RequestBody UserDTO userDTO) {
    return userService.update3(userDTO);
  }
  
  @DeleteMapping(value = "/delete")
  public Long deleteUser(@NotNull(message = "{message.notnull.id}") @RequestParam Long id) {
    return userService.deleteById(id);
  }
  
}
