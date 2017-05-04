package com.outr.arango.managed

import com.outr.arango.{ArangoCode, ArangoEdge, ArangoException, DocumentOption, Edge}
import com.outr.arango.rest.{CreateInfo, GraphResponse}
import io.circe.Encoder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class EdgeCollection[T <: Edge with DocumentOption](override val graph: Graph,
                                                             override val name: String,
                                                             from: List[String],
                                                             to: List[String]) extends AbstractCollection[T] {
  private lazy val edge: ArangoEdge = graph.instance.edge(name)

  override def create(waitForSync: Boolean = false): Future[GraphResponse] = edge.create(from, to, waitForSync)
  override def delete(): Future[GraphResponse] = edge.delete(dropCollection = true)

  override def get(key: String): Future[Option[T]] = edge[T](key).map(_.edge).recover {
    case t: ArangoException if t.error.errorCode == ArangoCode.ArangoDocumentNotFound => None
  }

  override protected def insertInternal(document: T): Future[CreateInfo] = {
    edge.insert[T](document).map(_.edge)
  }

  override protected def updateInternal[M](key: String, modification: M)
                                          (implicit encoder: Encoder[M]): Future[CreateInfo] = {
    edge.modify[M](key, modification).map(_.edge)
  }

  override protected def replaceInternal(document: T): Future[Unit] = {
    edge.replace[T](document._key.get, document).map(_ => ())
  }

  override protected def deleteInternal(key: String): Future[Boolean] = edge.delete(key).map(_.removed)
}
