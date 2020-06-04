package com.liukai.eshop.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author xiaoguogai
 * @since 2019-11-26
 */
@Data
@Accessors(chain = true)
public class Common {

  // private Long id;

  // @TableLogic
  // private Integer deleted;

  private Date createTime;

  private Date updateTime;

}
