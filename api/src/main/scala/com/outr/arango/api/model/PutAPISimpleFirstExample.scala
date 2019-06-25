package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleFirstExample(collection: String,
                                    example: Option[String] = None)