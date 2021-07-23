package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class ServerThreadsStruct(inProgress: Option[Int] = None,
                               queued: Option[Int] = None,
                               schedulerThreads: Option[Int] = None)

object ServerThreadsStruct {
  implicit val rw: ReaderWriter[ServerThreadsStruct] = ccRW
}