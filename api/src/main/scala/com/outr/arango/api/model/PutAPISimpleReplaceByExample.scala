package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleReplaceByExample(collection: String,
                                        example: Option[String] = None,
                                        newValue: Option[String] = None,
                                        options: Option[PutAPISimpleReplaceByExampleOptions] = None)