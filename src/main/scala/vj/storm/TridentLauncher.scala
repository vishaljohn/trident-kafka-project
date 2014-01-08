package vj.storm

import com.google.common.collect.ImmutableList
import backtype.storm.{Config => StormCfg, LocalCluster, StormSubmitter}
import backtype.storm.tuple.Fields
import storm.kafka.KafkaConfig
import storm.kafka.trident.{TransactionalTridentKafkaSpout, TridentKafkaConfig}
import storm.trident.TridentTopology
import storm.trident.testing.Split
import storm.trident.spout.IPartitionedTridentSpout
import backtype.storm.spout.RawMultiScheme
import com.google.common.collect.Lists
import storm.kafka.HostPort
import storm.kafka.BrokerHosts
import storm.kafka.ZkHosts
//import storm.kafka.KafkaConfig.ZkHosts


object TridentLauncher {

  def main(args: Array[String]): Unit = {
    val trident = new TridentTopology()
    val zookeepers = "10.29.29.208:2181"
    val zkHosts = new ZkHosts(zookeepers)
    val tridentKafkaConfig = new TridentKafkaConfig(zkHosts, "comments")
    tridentKafkaConfig.forceStartOffsetTime(-1)
    
    val spout = new TransactionalTridentKafkaSpout(tridentKafkaConfig)
    val stormConfig = new StormCfg
    val topology = trident.build()
    
  }

}