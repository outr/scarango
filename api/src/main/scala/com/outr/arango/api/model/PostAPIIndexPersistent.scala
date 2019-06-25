package com.outr.arango.api.model

import io.circe.Json


case class PostAPIIndexPersistent(`type`: String,
                                  fields: Option[List[String]] = None,
                                  sparse: Option[Boolean] = None,
                                  unique: Option[Boolean] = None)