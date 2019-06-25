package com.outr.arango.api.model

import io.circe.Json


case class V8ContextStruct(available: Option[Int] = None,
                           busy: Option[Int] = None,
                           dirty: Option[Int] = None,
                           free: Option[Int] = None,
                           max: Option[Int] = None)