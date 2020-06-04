package com.liukai.eshop.inventory.model;

import lombok.Data;

/**
 * 通用相应 model
 *
 * @author liukai
 */
@Data
public class Result {

  private Integer status;

  private String desc;

  private Object data;

  public static Result succ() {
    Result result = new Result();
    result.setResultCode(ResultCode.SUCCESS);
    return result;
  }

  public static Result succ(Object data) {
    Result result = new Result();
    result.setResultCode(ResultCode.SUCCESS);
    result.setData(data);
    return result;
  }

  public static Result fail(Integer status, String desc) {
    Result result = new Result();
    result.setStatus(status);
    result.setDesc(desc);
    return result;
  }

  public static Result fail(ResultCode resultCode) {
    Result result = new Result();
    result.setResultCode(resultCode);
    return result;
  }

  private void setResultCode(ResultCode resultCode) {
    this.status = resultCode.status();
    this.desc = resultCode.message();
  }

}
