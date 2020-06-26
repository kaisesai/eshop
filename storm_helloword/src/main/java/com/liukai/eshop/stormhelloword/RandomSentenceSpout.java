package com.liukai.eshop.stormhelloword;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

/**
 * 定义一个数据源：伪造一个假数据
 */
@Slf4j
public class RandomSentenceSpout extends BaseRichSpout {

  public static final String FIELD_SENTENCE = "sentence";

  private Random random;

  private SpoutOutputCollector collector;

  private String[] sentences;

  /**
   * 对 spout 进行初始化工作
   * 比如：创建一个线程池，创建一个数据库连接、构造一个 httpclient
   *
   * @param conf
   * @param context
   * @param collector
   */
  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    random = new Random();
    this.collector = collector;
    sentences = new String[] {"the cow jumped over the moon",
      "an apple a day keeps the doctor away", "four score and seven years ago",
      "snow white and the seven dwarfs", "i am at two with nature"};
    log.info("RandomSentenceSpout open");
  }

  /**
   * 本类 spout 最终会运行在 task 中，task 是会在某个 worker 进程的某个 executor 线程内部的。
   * <p>
   * 该 task 会负责无限循环调用 nextTuple 方法，就可以达到不断的发射最新的数据，形成一个数据流
   */
  @Override
  public void nextTuple() {
    Utils.sleep(2000);
    String sentence = this.sentences[random.nextInt(this.sentences.length)];
    log.error("RandomSentenceSpout sentence: {}", sentence);
    // 发射一条数据
    collector.emit(new Values(sentence));
  }

  /**
   * 定义发射出去的每个 tuple 中的每个 field 的名称
   * 这里只有一个值，只需要写一个字段名称
   *
   * @param declarer
   */
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields(FIELD_SENTENCE));
  }

}
