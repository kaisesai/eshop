package com.liukai.eshop.stormhelloword;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于对单词进行统计的 bolt
 */
@Slf4j
public class WordCountBolt extends BaseRichBolt {

  public static final String FIELD_WORDK = "wordk";

  public static final String FIELD_COUNT = "count";

  private OutputCollector collector;

  private Map<String, Integer> counts;

  /**
   * 初始化处理
   *
   * @param stormConf
   * @param context
   * @param collector
   */
  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    this.collector = collector;
    this.counts = new HashMap<>();
  }

  /**
   * 处理接收到的数据
   *
   * @param input
   */
  @Override
  public void execute(Tuple input) {
    // 记录并且统计出现的单词数据
    String word = input.getStringByField(SplitSentenceBolt.FIELD_WORD);
    // 如果散列表中没有单词数据，则初始单词次数为 1，如果有单词统计数据，则让单词次数加一
    counts.compute(word, (k, v) -> {
      if (v == null) {
        return 1;
      } else {
        return v + 1;
      }
    });
    Integer count = counts.get(word);
    log.error(Thread.currentThread().getName() + " WordCountBolt word: {}, count: {}", word,
              count);
    // 再将单词发射出去
    collector.emit(new Values(word, count));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields(FIELD_WORDK, FIELD_COUNT));
  }
}
