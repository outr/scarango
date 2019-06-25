package com.outr.arango.api.model

import io.circe.Json


case class PostAPIExportRestrictions(fields: Option[List[String]] = None,
                                     `type`: Option[String] = None)