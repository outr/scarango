package com.outr.arango.managed

import com.outr.arango._
import com.outr.arango.rest.{CreateInfo, GraphResponse, QueryResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class VertexCollection[T <: DocumentOption](override val graph: Graph,
                                                     override val name: String) extends AbstractCollection[T] {
  private lazy val vertex: ArangoVertex = graph.instance.vertex(name)

  override def create(): Future[GraphResponse] = vertex.create()
  override def delete(): Future[GraphResponse] = vertex.delete()

  override def byKey(key: String): Future[T] = vertex[T](key).map(_.vertex.get)

  override protected def insertInternal(document: T): Future[CreateInfo] = {
    vertex.insert[T](document, waitForSync = Some(true)).map(_.vertex)
  }

  override protected def replaceInternal(document: T): Future[Unit] = {
    vertex.replace[T](document._key.get, document).map(_ => ())
  }

  override protected def deleteInternal(document: T): Future[Boolean] = {
    vertex.delete(document._key.get).map(!_.error)
  }
}