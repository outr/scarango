package com.outr.arango.api.model

import com.outr.arango.Analyzer
import fabric._
import fabric.rw._

case class PostAPIViewProps(cleanupIntervalStep: Option[Long] = None,
                            commitIntervalMsec: Option[Long] = None,
                            consolidationIntervalMsec: Option[Long] = None,
                            consolidationPolicy: Option[PostAPIViewPropsConsolidation] = None,
                            links: Option[Map[String, ArangoLinkProperties]])

object PostAPIViewProps {
  implicit val linksRW: ReaderWriter[Map[String, ArangoLinkProperties]] = ReaderWriter(
    t => fabric.Obj(t.map {
      case (key, value) => key -> value.toValue
    }),
    v => v.asObj.value.map {
      case (key, value) => key -> value.as[ArangoLinkProperties]
    }
  )
  implicit val rw: ReaderWriter[PostAPIViewProps] = ccRW
}

case class ArangoLinkProperties(analyzers: List[Analyzer],
                                fields: Map[String, ArangoLinkFieldProperties],
                                includeAllFields: Boolean,
                                storeValues: String,
                                trackListPositions: Boolean)

object ArangoLinkProperties {
  implicit val linksRW: ReaderWriter[Map[String, ArangoLinkFieldProperties]] = ReaderWriter(
    t => fabric.Obj(t.map {
      case (key, value) => key -> value.toValue
    }),
    v => v.asObj.value.map {
      case (key, value) => key -> value.as[ArangoLinkFieldProperties]
    }
  )
  implicit val rw: ReaderWriter[ArangoLinkProperties] = ccRW
}

case class ArangoLinkFieldProperties(analyzers: List[Analyzer])

object ArangoLinkFieldProperties {
  implicit val rw: ReaderWriter[ArangoLinkFieldProperties] = ccRW
}