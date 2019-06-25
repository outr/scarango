package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleRemoveByKeysOpts(returnOld: Option[Boolean] = None,
                                        silent: Option[Boolean] = None,
                                        waitForSync: Option[Boolean] = None)