package com.outr.arango

import com.outr.arango.rest._
import io.youi.http.Method
import io.circe.generic.auto._

import scala.concurrent.Future

class ArangoGraphs(db: ArangoDB) {
  def list(): Future[GraphList] = {
    db.call[GraphList]("gharial", Method.Get)
  }

  def apply(name: String): ArangoGraph = new ArangoGraph(name, db)
}

class ArangoGraph(val name: String, val db: ArangoDB) {
  def create(orphanCollections: List[String] = Nil,
             edgeDefinitions: List[EdgeDefinition] = Nil,
             isSmart: Option[Boolean] = None,
             smartGraphAttribute: Option[String] = None,
             numberOfShards: Option[Int] = None): Future[GraphResponse] = {
    val options = if (smartGraphAttribute.nonEmpty || numberOfShards.nonEmpty) {
      Some(GraphOptions(smartGraphAttribute, numberOfShards))
    } else {
      None
    }
    val request = CreateGraphRequest(name, orphanCollections, edgeDefinitions, isSmart, options)
    db.restful[CreateGraphRequest, GraphResponse]("gharial", request)
  }

  def get(): Future[GraphResponse] = {
    db.call[GraphResponse](s"gharial/$name", Method.Get)
  }

  def listVertex(): Future[GraphCollectionList] = {
    db.call[GraphCollectionList](s"gharial/$name/vertex", Method.Get)
  }

  def listEdge(): Future[GraphCollectionList] = {
    db.call[GraphCollectionList](s"gharial/$name/edge", Method.Get)
  }

  def vertex(name: String): ArangoVertex = new ArangoVertex(name, this)

  def edge(name: String): ArangoEdge = new ArangoEdge(name, this)

  def delete(dropCollections: Boolean): Future[GraphDeleted] = {
    db.call[GraphDeleted](s"gharial/$name", Method.Delete, Map("dropCollections" -> dropCollections.toString))
  }
}

class ArangoVertex(name: String, graph: ArangoGraph) {
  def create(): Future[GraphResponse] = {
    graph.db.restful[AddVertexRequest, GraphResponse](s"gharial/${graph.name}/vertex", AddVertexRequest(name))
  }

  def delete(): Future[GraphResponse] = {
    graph.db.call[GraphResponse](s"gharial/${graph.name}/vertex/$name", Method.Delete)
  }
}

class ArangoEdge(name: String, graph: ArangoGraph) {
  def create(from: List[String], to: List[String]): Future[GraphResponse] = {
    graph.db.restful[EdgeDefinition, GraphResponse](s"gharial/${graph.name}/edge", EdgeDefinition(name, from, to))
  }

  def replace(from: List[String], to: List[String]): Future[GraphResponse] = {
    graph.db.restful[EdgeDefinition, GraphResponse](s"gharial/${graph.name}/edge/$name", EdgeDefinition(name, from, to), method = Method.Put)
  }

  def delete(): Future[GraphResponse] = {
    graph.db.call[GraphResponse](s"gharial/${graph.name}/edge/$name", Method.Delete)
  }
}