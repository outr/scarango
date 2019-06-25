package com.outr.arango.api.model

import io.circe.Json


case class AdminEchoServerStruct(address: Option[Int] = None,
                                 id: Option[String] = None,
                                 port: Option[Int] = None)