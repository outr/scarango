package com.outr.arango.api.model

import io.circe.Json


case class ServerThreadsStruct(inProgress: Option[Int] = None,
                               queued: Option[Int] = None,
                               schedulerThreads: Option[Int] = None)