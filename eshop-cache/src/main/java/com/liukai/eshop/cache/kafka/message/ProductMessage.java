package com.liukai.eshop.cache.kafka.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ProductMessage extends CommonMessage {

  private Long productId;

}
