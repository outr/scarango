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

class ArangoGraph(name: String, db: ArangoDB) {
  def create(orphanCollections: List[String] = Nil,
             edgeDefinitions: List[EdgeDefinition] = Nil,
             isSmart: Option[Boolean] = None,
             smartGraphAttribute: Option[String] = None,
             numberOfShards: Option[Int] = None): Future[CreateGraphResponse] = {
    val options = if (smartGraphAttribute.nonEmpty || numberOfShards.nonEmpty) {
      Some(GraphOptions(smartGraphAttribute, numberOfShards))
    } else {
      None
    }
    val request = CreateGraphRequest(name, orphanCollections, edgeDefinitions, isSmart, options)
    db.restful[CreateGraphRequest, CreateGraphResponse]("gharial", request)
  }

  def delete(dropCollections: Boolean): Future[GraphDeleted] = {
    db.call[GraphDeleted](s"gharial/$name", Method.Delete, Map("dropCollections" -> dropCollections.toString))
  }
}