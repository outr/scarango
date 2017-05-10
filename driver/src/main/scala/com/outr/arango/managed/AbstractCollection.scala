package com.outr.arango.managed

import com.outr.arango._
import com.outr.arango.rest.{CreateInfo, GraphResponse, QueryResponse}
import io.circe.{Decoder, Encoder}
import reactify.{Channel, TransformableChannel}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.experimental.macros

trait AbstractCollection[T <: DocumentOption] {
  def graph: Graph
  def name: String
  protected[managed] lazy val collection: ArangoCollection = graph.instance.db.collection(name)

  implicit val encoder: Encoder[T]
  implicit val decoder: Decoder[T]
  protected def updateDocument(document: T, info: CreateInfo): T

  lazy val inserting: TransformableChannel[T] = TransformableChannel[T]
  lazy val inserted: Channel[T] = Channel[T]
  lazy val upserting: TransformableChannel[T] = TransformableChannel[T]
  lazy val upserted: Channel[T] = Channel[T]
  lazy val updating: TransformableChannel[Modification] = TransformableChannel[Modification]
  lazy val updated: Channel[Modification] = Channel[Modification]
  lazy val replacing: TransformableChannel[Replacement[T]] = TransformableChannel[Replacement[T]]
  lazy val replaced: Channel[Replacement[T]] = Channel[Replacement[T]]
  lazy val deleting: TransformableChannel[String] = TransformableChannel[String]
  lazy val deleted: Channel[String] = Channel[String]

  graph.synchronized {
    graph.managedCollections = graph.managedCollections ::: List(this)
  }

  def create(waitForSync: Boolean = false): Future[GraphResponse]
  def delete(): Future[GraphResponse]
  def get(key: String): Future[Option[T]]
  final def apply(key: String): Future[T] = get(key).map(_.getOrElse(throw new RuntimeException(s"Key not found: $key.")))

  def index: ArangoIndexing = collection.index

  def insert(document: T): Future[T] = macro Macros.insert[T]
  def upsert(document: T): Future[T] = macro Macros.upsert[T]
  def update[M](key: String, modification: M)
               (implicit encoder: Encoder[M]): Future[CreateInfo] = macro Macros.update[T, M]
  def replace(document: T): Future[T] = macro Macros.replace[T]
  def replace(currentKey: String, document: T): Future[T] = macro Macros.replaceByKey[T]
  def delete(key: String): Future[Boolean] = {
    deleting.transform(key) match {
      case Some(modified) => {
        deleteInternal(modified).map { success =>
          deleted := modified
          success
        }
      }
      case None => Future.failed(new CancelledException("Delete cancelled."))
    }
  }

  object managed {
    def insert(document: T): Future[T] = {
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
    def upsert(document: T): Future[T] = {
      upserting.transform(document) match {
        case Some(modified) => {
          collection.document.upsert(modified)
        }
        case None => Future.failed(new CancelledException("Upsert cancelled."))
      }
    }
    def update[M](key: String, modification: M)
                 (implicit encoder: Encoder[M]): Future[CreateInfo] = {
      updating.transform(Modification(key, modification.asInstanceOf[AnyRef])) match {
        case Some(modified) => {
          updateInternal(modified.key, modified.update.asInstanceOf[M]).map { value =>
            updated := modified
            value
          }
        }
        case None => Future.failed(new CancelledException("Update cancelled."))
      }
    }
    def replace(replacement: Replacement[T]): Future[T] = {
      replacing.transform(replacement) match {
        case Some(modified) => {
          replaceInternal(modified.key, modified.replacement).map(_ => modified).map { value =>
            replaced := value
            value.replacement
          }
        }
        case None => Future.failed(new CancelledException("Replace cancelled."))
      }
    }
  }
  def cursor(query: Query, batchSize: Int = 100): Future[QueryResponse[T]] = {
    graph.cursor.apply[T](query, count = true, batchSize = Some(batchSize))
  }
  def paged(query: Query, batchSize: Int = 100): Future[QueryResponsePagination[T]] = {
    graph.cursor.paged[T](query, batchSize)
  }
  def iterator(query: Query, batchSize: Int = 100, timeout: FiniteDuration = 10.seconds): Iterator[T] = {
    val pagination = Await.result(paged(query, batchSize), timeout)
    new QueryResponseIterator[T](pagination, timeout)
  }
  def call(query: Query): Future[T] = graph.call[T](query)
  def first(query: Query): Future[Option[T]] = graph.first[T](query)
  lazy val allQuery: Query = Query(s"FOR x IN $name RETURN x", Map.empty)
  def all(batchSize: Int = 100): Future[QueryResponsePagination[T]] = paged(allQuery)

  protected def insertInternal(document: T): Future[CreateInfo]
  protected def updateInternal[M](key: String, modification: M)(implicit encoder: Encoder[M]): Future[CreateInfo]
  protected def replaceInternal(currentKey: String, document: T): Future[Unit]
  protected def deleteInternal(key: String): Future[Boolean]
}

case class Modification(key: String, update: AnyRef)

case class Replacement[T](key: String, replacement: T)