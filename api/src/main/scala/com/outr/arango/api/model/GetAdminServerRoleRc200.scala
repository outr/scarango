package com.outr.arango.api.model

import io.circe.Json


case class GetAdminServerRoleRc200(error: Boolean,
                                   code: Option[Long] = None,
                                   errorNum: Option[Long] = None,
                                   role: Option[String] = None)