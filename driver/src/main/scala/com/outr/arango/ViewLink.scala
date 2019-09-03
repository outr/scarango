package com.outr.arango

case class ViewLink(collectionName: String,
                    fields: List[String],
                    analyzers: List[String] = List("text_en"),     // TODO: better support config and default back to List("identity")
                    allowExists: Boolean = false,
                    trackListPositions: Boolean = false)
