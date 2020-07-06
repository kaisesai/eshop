package com.liukai.eshop.storm;

import com.alibaba.fastjson.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * 日志解析 Bolt
 */
public class LogParseBolt extends BaseRichBolt {

  private OutputCollector collector;

  @Override
  public void prepare(Map<String, Object> topoConf, TopologyContext context,
                      OutputCollector collector) {
    this.collector = collector;
  }

  @Override
  public void execute(Tuple input) {
    // 继续日志信息，提取出 productId 参数值
    String message = input.getStringByField("message");
    JSONObject jsonObject = JSONObject.parseObject(message);
    JSONObject uriArgs = jsonObject.getJSONObject("uri_args");
    Long productId = uriArgs.getLong("productId");
    if (productId != null) {
      collector.emit(new Values(productId));
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("productId"));
  }

}
