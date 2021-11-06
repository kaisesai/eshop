package com.liukai.eshop.common.model;

/**
 * @author liukai
 */

public enum ResultCode {
  /**
   * 成功
   */
  SUCCESS(0, "成功"),

  /**
   * 系统错误
   */
  SYSTEM_ERROR(500, "系统错误"),

  /**
   * 参数错误
   */
  PARAM_IS_INVALID(1000, "参数错误"),

  /**
   * 用户已存在
   */
  USER_IS_EXISTED(1001, "用户已存在");

  private final Integer status;

  private final String message;

  ResultCode(Integer status, String message) {
    this.status = status;
    this.message = message;
  }

  public Integer status() {
    return this.status;
  }

  public String message() {
    return this.message;
  }
}
