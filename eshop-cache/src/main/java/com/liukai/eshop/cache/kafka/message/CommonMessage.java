package com.liukai.eshop.cache.kafka.message;

import lombok.Data;

import java.util.Date;

/**
 * 通用消息类
 */
@Data
public class CommonMessage {

  private String id;

  private Date createTime;

  private Date updateTime;

}
