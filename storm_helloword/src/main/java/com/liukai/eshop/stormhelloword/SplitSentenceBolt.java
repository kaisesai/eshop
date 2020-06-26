package com.liukai.eshop.stormhelloword;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * 定义一个 bolt，用于对数据的加工
 * 这里拆分接收到的句子，拆分成一个一个的单词
 */
public class SplitSentenceBolt extends BaseRichBolt {

  public static final String FIELD_WORD = "word";

  private OutputCollector collector;

  /**
   * 该类初始化方法，这里可以拿到发射器
   *
   * @param stormConf
   * @param context
   * @param collector
   */
  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    this.collector = collector;
  }

  /**
   * 每接收到一条数据，就会调用该方法，进行加工处理
   *
   * @param input
   */
  @Override
  public void execute(Tuple input) {
    // 将句子拆分成一个一个的单词之后，再发射出去
    String sentence = input.getStringByField(RandomSentenceSpout.FIELD_SENTENCE);
    for (String word : sentence.split(" ")) {
      collector.emit(new Values(word));
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    // 定义数据的字段名称
    declarer.declare(new Fields(FIELD_WORD));
  }

}
