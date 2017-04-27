package com.outr.arango.managed

import com.outr.arango.{DocumentOption, Query}
import com.outr.arango.rest.{CreateInfo, GraphResponse, QueryResponse}
import io.circe.{Decoder, Encoder}
import reactify.{Channel, TransformableChannel}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AbstractCollection[T <: DocumentOption] {
  def graph: Graph
  def name: String

  protected implicit val encoder: Encoder[T]
  protected implicit val decoder: Decoder[T]
  protected def updateDocument(document: T, info: CreateInfo): T

  lazy val inserting: TransformableChannel[T] = TransformableChannel[T]
  lazy val inserted: Channel[T] = Channel[T]
  lazy val replacing: TransformableChannel[T] = TransformableChannel[T]
  lazy val replaced: Channel[T] = Channel[T]
  lazy val deleting: TransformableChannel[T] = TransformableChannel[T]
  lazy val deleted: Channel[T] = Channel[T]

  def create(): Future[GraphResponse]
  def delete(): Future[GraphResponse]
  def byKey(key: String): Future[T]
  final def insert(document: T): Future[T] = {
    inserting.transform(document) match {
      case Some(modified) => {
        insertInternal(modified).map(updateDocument(document, _)).map { value =>
          inserted := value
          value
        }
      }
      case None => Future.failed(new CancelledException("Insert cancelled."))
    }
  }
  final def replace(document: T): Future[T] = {
    replacing.transform(document) match {
      case Some(modified) => {
        replaceInternal(modified).map(_ => modified).map { value =>
          replaced := value
          value
        }
      }
      case None => Future.failed(new CancelledException("Replace cancelled."))
    }
  }
  final def delete(document: T): Future[Boolean] = {
    deleting.transform(document) match {
      case Some(modified) => {
        deleteInternal(modified).map { success =>
          deleted := modified
          success
        }
      }
      case None => Future.failed(new CancelledException("Delete cancelled."))
    }
  }
  def cursor(query: Query, batchSize: Int = 100): Future[QueryResponse[T]]
  def all(batchSize: Int = 100): Future[QueryResponse[T]] = cursor(Query(s"FOR x IN $name RETURN x", Map.empty))

  protected def insertInternal(document: T): Future[CreateInfo]
  protected def replaceInternal(document: T): Future[Unit]
  protected def deleteInternal(document: T): Future[Boolean]
}