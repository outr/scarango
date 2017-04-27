package com.outr.arango.managed

import com.outr.arango._
import com.outr.arango.rest.{CreateInfo, GraphResponse, QueryResponse}
import io.circe.{Decoder, Encoder}
import reactify.{Channel, TransformableChannel}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class Collection[T <: DocumentOption](graph: Graph, val name: String) {
  private lazy val vertex: ArangoVertex = graph.instance.vertex(name)

  protected implicit val encoder: Encoder[T]
  protected implicit val decoder: Decoder[T]
  protected def updateDocument(document: T, info: CreateInfo): T

  lazy val inserting: TransformableChannel[T] = TransformableChannel[T]
  lazy val inserted: Channel[T] = Channel[T]
  lazy val replacing: TransformableChannel[T] = TransformableChannel[T]
  lazy val replaced: Channel[T] = Channel[T]
  lazy val deleting: TransformableChannel[T] = TransformableChannel[T]
  lazy val deleted: Channel[T] = Channel[T]

  def create(): Future[GraphResponse] = vertex.create()
  def delete(): Future[GraphResponse] = vertex.delete()

  def byKey(key: String): Future[T] = vertex[T](key).map(_.vertex.get)

  def insert(document: T): Future[T] = {
    inserting.transform(document) match {
      case Some(modified) => {
        vertex.insert[T](modified, waitForSync = Some(true)).map(i => updateDocument(document, i.vertex)).map { value =>
          inserted := value
          value
        }
      }
      case None => Future.failed(new CancelledException("Insert cancelled."))
    }
  }

  def replace(document: T): Future[T] = {
    replacing.transform(document) match {
      case Some(modified) => {
        vertex.replace[T](modified._key.get, modified).map(_ => modified).map { value =>
          replaced := value
          value
        }
      }
      case None => Future.failed(new CancelledException("Replace cancelled."))
    }
  }

  def delete(document: T): Future[Boolean] = {
    deleting.transform(document) match {
      case Some(modified) => {
        vertex.delete(modified._key.get).map(!_.error).map { success =>
          deleted := modified
          success
        }
      }
      case None => Future.failed(new CancelledException("Delete cancelled."))
    }
  }

  def cursor(query: Query, batchSize: Int = 100): Future[QueryResponse[T]] = {
    graph.cursor.apply[T](query, count = true, batchSize = Some(batchSize))
  }
}