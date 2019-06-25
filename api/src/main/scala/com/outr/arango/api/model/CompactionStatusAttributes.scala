package com.outr.arango.api.model

import io.circe.Json


case class CompactionStatusAttributes(message: Option[String] = None,
                                      time: Option[String] = None)