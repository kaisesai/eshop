package com.liukai.eshop.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoguogai
 * @since 2019-11-26
 */
@Data
@Accessors(chain = true)
public class Common implements Serializable {

  protected Long id;

  // @TableLogic
  // private Integer deleted;

  protected Date createTime;

  protected Date updateTime;

}
