package com.outr.arango.core

import com.arangodb.async.ArangoDBAsync
import com.arangodb.entity.{LoadBalancingStrategy => LBS}

class ArangoDBServer(connection: ArangoDBAsync) {
  lazy val db: ArangoDB = new ArangoDB(connection.db())

  def db(name: String): ArangoDB = new ArangoDB(connection.db(name))
}

object ArangoDBServer {
  def apply(connection: ArangoDBAsync): ArangoDBServer = new ArangoDBServer(connection)

  def apply(config: ArangoDBConfig): ArangoDBServer = {
    val loadBalancingStrategy = config.loadBalancingStrategy match {
      case LoadBalancingStrategy.None => LBS.NONE
      case LoadBalancingStrategy.RoundRobin => LBS.ROUND_ROBIN
      case LoadBalancingStrategy.OneRandom => LBS.ONE_RANDOM
    }
    val builder = new ArangoDBAsync.Builder()
      .user(config.username)
      .password(config.password)
      .useSsl(config.ssl)
      .timeout(Option(config.timeout).map(_.toMillis.toInt).map(Integer.valueOf).orNull)
      .acquireHostList(config.acquireHostList)
      .chunksize(config.chunkSize match {
        case -1 => null
        case n => n
      })
      .connectionTtl(Option(config.connectionTtl).map(_.toMillis).map(java.lang.Long.valueOf).orNull)
      .keepAliveInterval(Option(config.keepAliveInterval).map(_.toMillis.toInt).map(Integer.valueOf).orNull)
      .loadBalancingStrategy(loadBalancingStrategy)
      .maxConnections(config.maxConnections)
    config.hosts.foreach { host =>
      builder.host(host.host, host.port)
    }
    apply(builder.build())
  }
}