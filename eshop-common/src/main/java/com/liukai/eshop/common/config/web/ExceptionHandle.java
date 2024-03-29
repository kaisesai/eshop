package com.liukai.eshop.common.config.web;

import com.liukai.eshop.common.model.Result;
import com.liukai.eshop.common.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一异常处理器
 *
 * @author liukai
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandle {
  
  @ExceptionHandler(value = Exception.class)
  public Result<Object> handle(Exception e) {
    // if(exception instanceof SbException) {
    //   SbException sbexception = (SbException)exception;
    //   return ReturnMessageUtil.error(sbexception.getCode(), sbexception.getMessage());
    // }else {
    //   logger.error("系统异常 {}",exception);
    //   return ReturnMessageUtil.error(-1, "未知异常"+exception.getMessage());
    // }
    log.error("系统异常:{}", e.getMessage(), e);
    return Result.fail(ResultCode.SYSTEM_ERROR, e.getMessage());
  }
  
  @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class,
                      TypeMismatchException.class, HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<Object> handleBadRequestException(Exception exception, HttpServletRequest request) {
    log.error("参数异常", exception);
    return Result.fail(ResultCode.PARAM_IS_INVALID, exception.getMessage());
    // return ApiResp.error(new Status(Error.ErrorCode.INVALID_PARAMETER, exception.getMessage()));
  }

}
