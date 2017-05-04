package com.outr.arango.managed

import com.outr.arango._
import com.outr.arango.rest.{CreateInfo, GraphResponse, QueryResponse}
import io.circe.Encoder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class VertexCollection[T <: DocumentOption](override val graph: Graph,
                                                     override val name: String) extends AbstractCollection[T] {
  private lazy val vertex: ArangoVertex = graph.instance.vertex(name)

  override def create(waitForSync: Boolean = false): Future[GraphResponse] = vertex.create(waitForSync)
  override def delete(): Future[GraphResponse] = vertex.delete()

  override def get(key: String): Future[Option[T]] = vertex[T](key).map(_.vertex).recover {
    case t: ArangoException if t.error.errorCode == ArangoCode.ArangoDocumentNotFound => None
  }

  override protected def insertInternal(document: T): Future[CreateInfo] = {
    vertex.insert[T](document, waitForSync = Some(true)).map(_.vertex)
  }

  override protected def updateInternal[M](key: String, modification: M)
                                          (implicit encoder: Encoder[M]): Future[CreateInfo] = {
    vertex.modify[M](key, modification).map(_.vertex)
  }

  override protected def replaceInternal(document: T): Future[Unit] = {
    vertex.replace[T](document._key.get, document).map(_ => ())
  }

  override protected def deleteInternal(key: String): Future[Boolean] = vertex.delete(key).map(_.removed)
}