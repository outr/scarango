package com.outr.arango.rest

case class GraphList(error: Boolean,
                     graphs: List[GraphEntry],
                     code: Int)

case class GraphEntry(_key: Option[String],
                      _id: String,
                      _rev: String,
                      name: String,
                      isSmart: Boolean,
                      numberOfShards: Int,
                      smartGraphAttribute: String,
                      orphanCollections: List[String],
                      edgeDefinitions: List[EdgeDefinition])

case class EdgeDefinition(collection: String, from: List[String], to: List[String])

case class CreateGraphRequest(name: String,
                              orphanCollections: List[String] = Nil,
                              edgeDefinitions: List[EdgeDefinition] = Nil,
                              isSmart: Option[Boolean] = None,
                              options: Option[GraphOptions])

case class GraphOptions(smartGraphAttribute: Option[String],
                        numberOfShards: Option[Int])

case class CreateGraphResponse(error: Boolean,
                               graph: GraphEntry,
                               code: Int)

case class GraphDeleted(error: Boolean, removed: Boolean, code: Int)