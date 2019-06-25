package com.outr.arango.api.model

import io.circe.Json


case class ServerStatisticsStruct(physicalMemory: Option[Int] = None,
                                  threads: Option[ServerThreadsStruct] = None,
                                  uptime: Option[Int] = None,
                                  v8Context: Option[V8ContextStruct] = None)