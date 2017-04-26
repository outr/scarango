package com.outr.arango.managed

import com.outr.arango._
import com.outr.arango.rest.{CreateInfo, GraphResponse, QueryResponse, VertexInsert}
import io.circe.{Decoder, Encoder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class Collection[T <: DocumentOption](graph: Graph, val name: String) {
  private lazy val vertex: ArangoVertex = graph.instance.vertex(name)

  protected implicit val encoder: Encoder[T]
  protected implicit val decoder: Decoder[T]
  protected def updateDocument(document: T, info: CreateInfo): T

  def create(): Future[GraphResponse] = vertex.create()
  def delete(): Future[GraphResponse] = vertex.delete()

  def byKey(key: String): Future[T] = vertex[T](key).map(_.vertex.get)

  def insert(document: T): Future[T] = {
    vertex.insert[T](document, waitForSync = Some(true)).map(i => updateDocument(document, i.vertex))
  }

  def replace(document: T): Future[T] = {
    vertex.replace[T](document._key.get, document).map(_ => document)
  }

  def delete(document: T): Future[Boolean] = vertex.delete(document._key.get).map(!_.error)

  def cursor(query: Query, batchSize: Int = 100): Future[QueryResponse[T]] = {
    graph.cursor.apply[T](query, count = true, batchSize = Some(batchSize))
  }
}