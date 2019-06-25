package com.outr.arango.api.model

import io.circe.Json


case class DeleteAPIAqlfunctionRc200(error: Boolean,
                                     code: Option[Long] = None,
                                     deletedCount: Option[Long] = None)