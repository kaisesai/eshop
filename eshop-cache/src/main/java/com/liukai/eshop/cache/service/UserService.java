package com.liukai.eshop.cache.service;

import com.liukai.eshop.cache.model.dto.UserDTO;
import com.liukai.eshop.model.entity.User;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

@Validated(value = Default.class)
public interface UserService {
  
  User getByIdFromCache(Long id);
  
  User getByIdFromCache2(Long id);
  
  User getById(Long id);
  
  Long deleteById(@Min(value = 1, message = "{message.notnull.id}") Long id);
  
  User update(UserDTO userDTO);
  
  User update2(UserDTO userDTO);
  
  User update3(@NotNull @Valid UserDTO userDTO);
  
  User save(UserDTO userDTO);
  
}
