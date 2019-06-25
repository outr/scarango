package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleUpdateByExample(collection: String,
                                       example: Option[String] = None,
                                       newValue: Option[Json] = None,
                                       options: Option[PutAPISimpleUpdateByExampleOptions] = None)