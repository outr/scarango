package com.outr.arango.api.model

import io.circe.Json


case class RestRemoveByKeys(collection: String,
                            keys: Option[List[String]] = None,
                            options: Option[PutAPISimpleRemoveByKeysOpts] = None)