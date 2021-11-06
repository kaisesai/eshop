package com.liukai.eshop.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * (User)实体类
 *
 * @author liukai
 * @since 2020-06-03 19:27:43
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class User extends Common {
  
  public static final User DEFAULT_INSTANCE = new User();
  
  private static final long serialVersionUID = 410237152578201438L;
  
  static {
    DEFAULT_INSTANCE.setId(-1L);
    DEFAULT_INSTANCE.setAge(-1);
    DEFAULT_INSTANCE.setName("无");
    DEFAULT_INSTANCE.setCreateTime(new Date());
    DEFAULT_INSTANCE.setUpdateTime(new Date());
  }
  
  /**
   * 主键
   */
  // private Long id;
  
  /**
   * 姓名
   */
  private String name;
  
  /**
   * 年龄
   */
  private Integer age;
  
}
