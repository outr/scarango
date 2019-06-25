package com.outr.arango.api.model

import io.circe.Json


case class GetAPIDatabaseNew(name: String,
                             users: Option[GetAPIDatabaseNewUSERS] = None)