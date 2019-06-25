package com.outr.arango.api.model

import io.circe.Json


case class PostAPIIndexGeo(`type`: String,
                           fields: Option[List[String]] = None,
                           geoJson: Option[String] = None)