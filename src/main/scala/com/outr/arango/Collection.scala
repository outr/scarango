package com.outr.arango

import com.outr.arango.rest.{GraphResponse, VertexInsert}
import io.circe.{Decoder, Encoder}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class Collection[T <: DocumentOption](graph: ArangoGraph, val name: String) {
  private lazy val vertex: ArangoVertex = graph.vertex(name)

  protected implicit def encoder: Encoder[T]
  protected implicit def decoder: Decoder[T]
  protected def updateDocument(document: T, insert: VertexInsert): T

  def create(): Future[GraphResponse] = vertex.create()
  def delete(): Future[GraphResponse] = vertex.delete()

  def byId(id: String): Future[T] = vertex[T](id).map(_.vertex.get)

  def insert(document: T): Future[T] = {
    vertex.insert[T](document, waitForSync = Some(true)).map(updateDocument(document, _))
  }

  def replace(document: T): Future[T] = {
    vertex.replace[T](document._key.get, document).map(_ => document)
  }

  def delete(document: T): Future[Boolean] = vertex.delete(document._key.get).map(!_.error)
}