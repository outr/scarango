package com.outr.arango.api.model

import io.circe.Json


case class GetAPIDatabaseNewUSERS(active: Option[Boolean] = None,
                                  extra: Option[Json] = None,
                                  passwd: Option[String] = None,
                                  username: Option[String] = None)