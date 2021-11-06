package com.liukai.eshop.cache.kafka;

import com.liukai.eshop.cache.kafka.message.CommonMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgProducer {

  // @Autowired
  private KafkaTemplate<Object, Object> kafkaTemplate;

  public void produceMsg(String topic, CommonMessage msg) {
    log.info("produce msg, msg:{}, topic:{}", msg, topic);
    kafkaTemplate.send(topic, msg);
  }

}
