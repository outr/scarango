package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class HTTPAPITRAVERSAL(startVertex: String,
                            direction: Option[String] = None,
                            edgeCollection: Option[String] = None,
                            expander: Option[String] = None,
                            filter: Option[String] = None,
                            graphName: Option[String] = None,
                            init: Option[String] = None,
                            itemOrder: Option[String] = None,
                            maxDepth: Option[String] = None,
                            maxIterations: Option[String] = None,
                            minDepth: Option[String] = None,
                            order: Option[String] = None,
                            sort: Option[String] = None,
                            strategy: Option[String] = None,
                            uniqueness: Option[String] = None,
                            visitor: Option[String] = None)

object HTTPAPITRAVERSAL {
  implicit val rw: ReaderWriter[HTTPAPITRAVERSAL] = ccRW
}