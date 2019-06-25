package com.outr.arango.api.model

import io.circe.Json


case class PostAPINewTasks(name: String,
                           command: Option[String] = None,
                           offset: Option[Long] = None,
                           params: Option[String] = None,
                           period: Option[Long] = None)