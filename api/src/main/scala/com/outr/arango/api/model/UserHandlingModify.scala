package com.outr.arango.api.model

import io.circe.Json


case class UserHandlingModify(passwd: String,
                              active: Option[Boolean] = None,
                              extra: Option[Json] = None)