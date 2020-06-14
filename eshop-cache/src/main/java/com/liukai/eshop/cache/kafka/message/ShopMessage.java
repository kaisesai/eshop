package com.liukai.eshop.cache.kafka.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ShopMessage extends CommonMessage {

  private Long shopId;

  private Long productId;

}
