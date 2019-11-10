package com.outr.arango.api.model

import com.outr.arango.Analyzer

case class PostAPIViewProps(cleanupIntervalStep: Option[Long] = None,
                            commitIntervalMsec: Option[Long] = None,
                            consolidationIntervalMsec: Option[Long] = None,
                            consolidationPolicy: Option[PostAPIViewPropsConsolidation] = None,
                            links: Option[Map[String, ArangoLinkProperties]])

case class ArangoLinkProperties(analyzers: List[Analyzer],
                                fields: Map[String, ArangoLinkFieldProperties],
                                includeAllFields: Boolean,
                                storeValues: String,
                                trackListPositions: Boolean)

case class ArangoLinkFieldProperties(analyzers: List[Analyzer])