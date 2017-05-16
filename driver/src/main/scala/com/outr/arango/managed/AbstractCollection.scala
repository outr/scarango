package com.outr.arango.managed

import com.outr.arango._
import com.outr.arango.rest._
import io.circe.{Decoder, Encoder}
import reactify.{Channel, Observable, TransformableChannel}

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

  lazy val triggers: Triggers[T] = new Triggers(this)

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
  def modify(original: T, modified: T): Future[CreateInfo] = macro Macros.modify[T]
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
    def modify(original: T, modified: T): Future[CreateInfo] = {
      val originalJson = encoder(original)
      val modifiedJson = encoder(modified)
      val diff = Diff.diff(originalJson, modifiedJson)
      update(original._key.get, diff)
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

class Triggers[T <: DocumentOption](collection: AbstractCollection[T]) {
  lazy val events: Observable[LogEvent] = {
    val c = Channel[LogEvent]
    collection.graph.realTime.events.attach { event =>
      if (event.cname.contains(collection.name)) {
        c := event
      }
    }
    c
  }

  private def typed(eventTypes: EventType*): Observable[T] = {
    val c = Channel[T]
    events.attach { event =>
      if (eventTypes.contains(event.eventType)) {
        val data = event.data.getOrElse(throw new RuntimeException(s"Data was null for DocumentUpsert!"))
        val value = collection.decoder.decodeJson(data) match {
          case Left(error) => throw new RuntimeException(s"JSON decoding error: $data", error)
          case Right(result) => result
        }
        c := value
      }
    }
    c
  }

  lazy val upsert: Observable[T] = typed(EventType.DocumentUpsert, EventType.EdgeUpsert)
  lazy val deletion: Observable[T] = typed(EventType.Deletion)
}