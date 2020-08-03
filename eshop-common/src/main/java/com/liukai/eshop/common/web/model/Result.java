package com.liukai.eshop.common.web.model;

import lombok.Data;

/**
 * 通用响应 model
 *
 * @author liukai
 */
@Data
public class Result<T> {

  private Integer status;

  private String statusDesc;

  private String errorMsg;

  private T data;

  public static Result<Object> succ() {
    Result<Object> result = new Result<>();
    result.setResultCode(ResultCode.SUCCESS);
    return result;
  }

  public static <T> Result<T> succ(T data) {
    Result<T> result = new Result<>();
    result.setResultCode(ResultCode.SUCCESS);
    result.setData(data);
    return result;
  }

  public static Result<Object> fail(Integer status, String desc, String errorMsg) {
    Result<Object> result = new Result<>();
    result.setStatus(status);
    result.setStatusDesc(desc);
    result.setErrorMsg(errorMsg);
    return result;
  }

  public static Result<Object> fail(ResultCode resultCode) {
    Result<Object> result = new Result<>();
    result.setResultCode(resultCode);
    return result;
  }

  public static Result<Object> fail(ResultCode resultCode, String errorMsg) {
    Result<Object> result = new Result<>();
    result.setResultCode(resultCode);
    result.setErrorMsg(errorMsg);
    return result;
  }

  private void setResultCode(ResultCode resultCode) {
    this.status = resultCode.status();
    this.statusDesc = resultCode.message();
  }

}
