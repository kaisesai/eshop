package com.liukai.eshop.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.shade.org.apache.commons.lang.ArrayUtils;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 * 热点商品拓扑程序
 */
public class HotProductTopology {

  public static void main(String[] args) throws Exception {
    TopologyBuilder builder = new TopologyBuilder();
    // 设置 AccessLogConsumerSpout，以及并发量
    builder.setSpout(AccessLogConsumerSpout.class.getSimpleName(), new AccessLogConsumerSpout(), 2);
    // 设置 LogParseBolt
    builder.setBolt(LogParseBolt.class.getSimpleName(), new LogParseBolt(), 5).setNumTasks(5)
      .shuffleGrouping(AccessLogConsumerSpout.class.getSimpleName());
    // 设置 ProductCountBolt
    builder.setBolt(ProductCountBolt.class.getSimpleName(), new ProductCountBolt(), 5)
      .setNumTasks(5).fieldsGrouping(LogParseBolt.class.getSimpleName(), new Fields("productId"));

    Config config = new Config();
    config.setDebug(false);
    if (ArrayUtils.isNotEmpty(args)) {
      config.setNumWorkers(3);
      // 提交到 storm 集群中
      StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());
    } else {
      // 本地运行
      config.setMaxTaskParallelism(3);
      LocalCluster cluster = new LocalCluster();
      cluster
        .submitTopology(HotProductTopology.class.getSimpleName(), config, builder.createTopology());
      Utils.sleep(2000000);
      // cluster.killTopology(HotProductTopology.class.getSimpleName());
      cluster.shutdown();
    }
  }

}
