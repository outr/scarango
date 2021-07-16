package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GraphRepresentation(Id: Option[String] = None,
                               Rev: Option[String] = None,
                               edgeDefinitions: Option[GraphEdgeDefinition] = None,
                               isSmart: Option[Boolean] = None,
                               name: Option[String] = None,
                               numberOfShards: Option[Int] = None,
                               orphanCollections: Option[List[String]] = None,
                               replicationFactor: Option[Int] = None,
                               smartGraphAttribute: Option[String] = None)

object GraphRepresentation {
  implicit val rw: ReaderWriter[GraphRepresentation] = ccRW
}