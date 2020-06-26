package com.liukai.eshop.stormhelloword;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.shade.org.apache.commons.lang.ArrayUtils;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 * 需求：统计一些句子中单词出现的频率
 */
public class WordCountTopology {

  public static void main(String[] args) throws Exception {
    // 构建拓扑，手动定义业务逻辑
    // 其他的提交到 storm 集群后，由 storm 去调度在哪些机器上启动你所定义的拓扑
    TopologyBuilder builder = new TopologyBuilder();

    // 设置 RandomSentenceSpout，定义 id、spout、并发数量
    builder.setSpout(RandomSentenceSpout.class.getSimpleName(), new RandomSentenceSpout(), 2);

    // 设置 SplitSentenceBolt
    builder.setBolt(SplitSentenceBolt.class.getSimpleName(), new SplitSentenceBolt(), 5)
      // 默认一个 executor 一个 task
      // 这里设置 5 个 executor，但是 task 设置了 10 个，相当于每个 executor 2 个 task
      .setNumTasks(10)
      // 配置该 bolt 以何种方式从哪里获取数据
      .shuffleGrouping(RandomSentenceSpout.class.getSimpleName());

    // 设置 WordCountBolt
    builder.setBolt(WordCountBolt.class.getSimpleName(), new WordCountBolt(), 5).setNumTasks(10)
      // 配置按字段形式去从 SplitSentenceBolt 中获取数据
      // 相同的单词始终都会被发射到同一个 task 中
      .fieldsGrouping(SplitSentenceBolt.class.getSimpleName(),
                      new Fields(SplitSentenceBolt.FIELD_WORD));

    Config config = new Config();
    config.setDebug(false);
    if (ArrayUtils.isNotEmpty(args)) {
      // 表示是在命令行中运行的，需要提交到 storm 集群中
      config.setNumWorkers(3);
      StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());
    } else {
      // 本环境启动
      // 设置最大任务并行度
      config.setMaxTaskParallelism(3);
      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("word-count", config, builder.createTopology());
      // 休眠 10 秒
      Utils.sleep(10000);
      cluster.shutdown();
    }

  }

}
