package com.liukai.eshop.common.web.config;

import com.liukai.eshop.common.web.model.Result;
import com.liukai.eshop.common.web.model.ResultCode;
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
  public Result handle(Exception exception) {
    // if(exception instanceof SbException) {
    //   SbException sbexception = (SbException)exception;
    //   return ReturnMessageUtil.error(sbexception.getCode(), sbexception.getMessage());
    // }else {
    //   logger.error("系统异常 {}",exception);
    //   return ReturnMessageUtil.error(-1, "未知异常"+exception.getMessage());
    // }
    log.error("系统异常", exception);
    return Result.fail(ResultCode.SYSTEM_ERROR);
  }

  @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class,
                      TypeMismatchException.class, HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result handleBadRequestException(Exception ex, HttpServletRequest request) {
    log.error("参数异常", ex);
    return Result.fail(ResultCode.PARAM_IS_INVALID);
    // return ApiResp.error(new Status(Error.ErrorCode.INVALID_PARAMETER, ex.getMessage()));
  }

}
