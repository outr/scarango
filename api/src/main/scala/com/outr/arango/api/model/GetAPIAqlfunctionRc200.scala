package com.outr.arango.api.model

import io.circe.Json


case class GetAPIAqlfunctionRc200(error: Boolean,
                                  code: Option[Long] = None,
                                  result: Option[AqlUserfunctionStruct] = None)