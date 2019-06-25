package com.outr.arango.api.model

import io.circe.Json


case class PostAPIAqlfunctionRc200(error: Boolean,
                                   code: Option[Long] = None,
                                   isNewlyCreated: Option[Boolean] = None)