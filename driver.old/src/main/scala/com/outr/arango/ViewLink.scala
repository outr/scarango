package com.outr.arango

import com.outr.arango.api.model.ArangoLinkFieldProperties

case class ViewLink(collectionName: String,
                    fields: Map[String, ArangoLinkFieldProperties],
                    analyzers: List[Analyzer] = List(Analyzer.Identity),
                    allowExists: Boolean = false,
                    trackListPositions: Boolean = false)