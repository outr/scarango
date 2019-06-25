package com.outr.arango.api.model

import io.circe.Json


case class GetAPIReturnRc200(server: String,
                             details: Option[VersionDetailsStruct] = None,
                             version: Option[String] = None)