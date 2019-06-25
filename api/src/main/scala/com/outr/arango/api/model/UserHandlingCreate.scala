package com.outr.arango.api.model

import io.circe.Json


case class UserHandlingCreate(user: String,
                              active: Option[Boolean] = None,
                              extra: Option[Json] = None,
                              passwd: Option[String] = None)