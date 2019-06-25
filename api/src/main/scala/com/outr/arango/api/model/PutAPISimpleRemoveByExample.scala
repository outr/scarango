package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleRemoveByExample(collection: String,
                                       example: Option[String] = None,
                                       options: Option[PutAPISimpleRemoveByExampleOpts] = None)