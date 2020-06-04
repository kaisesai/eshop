package com.liukai.eshop.inventory.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * (User)实体类
 *
 * @author liukai
 * @since 2020-06-03 19:27:43
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class User extends Common implements Serializable {

  private static final long serialVersionUID = 410237152578201438L;

  /**
   * 主键
   */
  private Long id;

  /**
   * 姓名
   */
  private String name;

  /**
   * 年龄
   */
  private Integer age;

}
