package com.outr.arango.rest

case class GraphList(error: Boolean,
                     graphs: List[GraphEntry],
                     code: Int)

case class GraphEntry(_key: Option[String],
                      _id: String,
                      _rev: String,
                      name: Option[String],
                      isSmart: Boolean,
                      numberOfShards: Int,
                      smartGraphAttribute: String,
                      orphanCollections: List[String],
                      edgeDefinitions: List[EdgeDefinition])

case class EdgeDefinition(collection: String, from: List[String], to: List[String])

case class CreateGraphRequest(name: String,
                              orphanCollections: List[String] = Nil,
                              edgeDefinitions: List[EdgeDefinition] = Nil,
                              isSmart: Boolean) //,
//                              options: Excludable[GraphOptions])    // TODO: add back once null / exclude bug is fixed

case class GraphOptions(smartGraphAttribute: Option[String],
                        numberOfShards: Option[Int])

case class GraphResponse(error: Boolean,
                         graph: GraphEntry,
                         code: Int)

case class GraphCollectionList(error: Boolean, collections: List[String], code: Int)

case class DeleteResponse(error: Boolean, removed: Boolean, code: Int)

case class AddVertexRequest(collection: String)

case class VertexInsert(error: Boolean, vertex: CreateInfo, code: Int)

case class CreateInfo(_id: String, _key: String, _rev: String)

case class VertexResult[T](error: Boolean, vertex: Option[T], code: Int)

case class EdgeInsert(error: Boolean, edge: CreateInfo, code: Int)

case class EdgeResult[T](error: Boolean, edge: Option[T], code: Int)