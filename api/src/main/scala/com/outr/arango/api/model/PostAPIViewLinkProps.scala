package com.outr.arango.api.model

import io.circe.Json


case class PostAPIViewLinkProps(analyzers: Option[List[String]] = None,
                                fields: Option[List[PostAPIViewFields]] = None,
                                includeAllFields: Option[Boolean] = None,
                                storeValues: Option[String] = None,
                                trackListPositions: Option[Boolean] = None)