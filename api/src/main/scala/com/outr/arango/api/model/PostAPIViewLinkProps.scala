package com.outr.arango.api.model

import com.outr.arango.Analyzer

case class PostAPIViewLinkProps(analyzers: Option[List[Analyzer]] = None,
                                fields: Option[List[PostAPIViewFields]] = None,
                                includeAllFields: Option[Boolean] = None,
                                storeValues: Option[String] = None,
                                trackListPositions: Option[Boolean] = None)