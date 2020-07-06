package com.liukai.eshop.storm;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedTransferQueue;

/**
 * 访问日志消费 kafka 的 spout
 */
@Slf4j
public class AccessLogConsumerSpout extends BaseRichSpout {

  private final LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

  private final String topic = "access-log";

  private SpoutOutputCollector collector;

  private KafkaConsumer<String, String> kafkaConsumer;

  // public static void main(String[] args) {
  // AccessLogConsumerSpout spout = new AccessLogConsumerSpout();
  // spout.startKafka();
  // spout.open(null, null, null);
  //
  // while (!Thread.currentThread().isInterrupted()) {
  //   spout.nextTuple();
  // }
  // }

  @Override
  public void open(Map<String, Object> conf, TopologyContext context,
                   SpoutOutputCollector collector) {
    this.collector = collector;
    // 启动 kafka
    startKafka();
  }

  private void startKafka() {
    // 启动一个线程，初始化 kafka 客户端消费 topic
    initKafka();

    // 启动一个线程，初始化 kafka 连接
    // 监听 topic ，将数据放入阻塞队列中
    new Thread(() -> {
      // 订阅 topic
      kafkaConsumer.subscribe(Collections.singletonList(topic));
      // 无限循环获取消息
      while (true) {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records) {
          log.info("监听到 topic 为 {} 的数据， offset = {}, key = {}, value = {}", topic, record.offset(),
                   record.key(), record.value());
          // 将数据写入阻塞队列，并同步数据
          try {
            queue.transfer(record.value());
            kafkaConsumer.commitSync();
          } catch (InterruptedException e) {
            log.warn("InterruptedException", e);
            Thread.currentThread().interrupt();
            break;
          }
        }
      }

    }).start();

  }

  private void initKafka() {
    Properties props = new Properties();
    props.setProperty("bootstrap.servers", "localhost:9092");
    props.setProperty("enable.auto.commit", "false");
    props
      .setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.setProperty("value.deserializer",
                      "org.apache.kafka.common.serialization.StringDeserializer");
    props.setProperty("group.id", "eshop-cache-group");
    props.setProperty("session.timeout.ms", "40000");
    props.setProperty("auto.commit.interval.ms", "1000");

    kafkaConsumer = new KafkaConsumer<>(props);
  }

  @Override
  public void nextTuple() {
    try {
      // 使用 LinkedTransferQueue 的目的：
      // kafka put 会一直阻塞，直到有一个 take 执行，才会返回
      // 这里能真实反映客户端消费 kafka 的能力
      // 而不是无限消费，存在内存中
      String message = queue.take();
      log.info("从队列中消费数据：{}", message);
      collector.emit(new Values(message));
    } catch (Exception e) {
      log.error("AccessLogConsumerSpout 获取数据异常", e);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("message"));
  }

}
