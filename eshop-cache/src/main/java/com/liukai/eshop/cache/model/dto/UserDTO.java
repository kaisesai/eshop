package com.liukai.eshop.cache.model.dto;

import com.liukai.eshop.cache.config.group.Insert;
import com.liukai.eshop.cache.config.group.Update;
import com.liukai.eshop.model.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

@EqualsAndHashCode(callSuper = false)
@Data
public class UserDTO extends User {
  
  @NotNull(groups = {Insert.class, Update.class, Default.class}, message = "message.notnull.id")
  private Long myId;
  
  public User convertToUser() {
    User user = new User();
    BeanUtils.copyProperties(this, user);
    return user;
  }
  
}
