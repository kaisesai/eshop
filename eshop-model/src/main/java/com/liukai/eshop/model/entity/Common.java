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

  // private Long id;

  // @TableLogic
  // private Integer deleted;

  private Date createTime;

  private Date updateTime;

}
