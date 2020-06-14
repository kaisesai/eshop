package com.liukai.eshop.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ShopInfo extends Common  {

  private String name;

  private Integer level;

  private Double goodCommentRate;
}
