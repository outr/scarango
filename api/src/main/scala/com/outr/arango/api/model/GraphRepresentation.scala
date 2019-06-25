package com.outr.arango.api.model

import io.circe.Json


case class GraphRepresentation(Id: Option[String] = None,
                               Rev: Option[String] = None,
                               edgeDefinitions: Option[GraphEdgeDefinition] = None,
                               isSmart: Option[Boolean] = None,
                               name: Option[String] = None,
                               numberOfShards: Option[Int] = None,
                               orphanCollections: Option[List[String]] = None,
                               replicationFactor: Option[Int] = None,
                               smartGraphAttribute: Option[String] = None)