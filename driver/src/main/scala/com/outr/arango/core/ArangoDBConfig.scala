package com.outr.arango.core

import scala.concurrent.duration.FiniteDuration

case class ArangoDBConfig(username: String = null,
                          password: String = null,
                          ssl: Boolean = false,
                          timeout: FiniteDuration = null,
                          acquireHostList: Boolean = false,
                          chunkSize: Int = -1,
                          connectionTtl: FiniteDuration = null,
                          hosts: List[Host] = List(Host()),
                          keepAliveInterval: FiniteDuration = null,
                          loadBalancingStrategy: LoadBalancingStrategy = LoadBalancingStrategy.None,
                          maxConnections: Int = 1)