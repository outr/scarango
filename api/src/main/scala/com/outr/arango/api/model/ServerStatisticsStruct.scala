package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class ServerStatisticsStruct(physicalMemory: Option[Int] = None,
                                  threads: Option[ServerThreadsStruct] = None,
                                  uptime: Option[Int] = None,
                                  v8Context: Option[V8ContextStruct] = None)

object ServerStatisticsStruct {
  implicit val rw: ReaderWriter[ServerStatisticsStruct] = ccRW
}