package com.outr.arango.managed

import java.util.concurrent.atomic.AtomicBoolean

import com.outr.arango.rest.LogEvent
import com.outr.arango.{Arango, ArangoCursor, ArangoDB, ArangoGraph, ArangoSession, Credentials, DocumentOption, Edge, Macros, Query, ReplicationMonitor}
import io.circe.Decoder
import io.youi.net.URL
import reactify.{Channel, Observable}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.experimental.macros

class Graph(name: String,
            db: String = Arango.defaultDatabase,
            url: URL = Arango.defaultURL,
            credentials: Option[Credentials] = Arango.defaultCredentials,
            timeout: FiniteDuration = 15.seconds) {
  private[managed] lazy val arango: Arango = new Arango(url)
  private[managed] lazy val sessionFuture: Future[ArangoSession] = arango.session(credentials)
  private[managed] lazy val dbFuture: Future[ArangoDB] = sessionFuture.map(_.db(db))
  private[managed] lazy val graphFuture: Future[ArangoGraph] = dbFuture.map(_.graph(name))

  private[managed] lazy val instance: ArangoGraph = Await.result[ArangoGraph](graphFuture, timeout)

  private[managed] var managedCollections = List.empty[AbstractCollection[_]]
  private[managed] lazy val monitor: ReplicationMonitor = instance.db.replication.monitor
  def collections: List[AbstractCollection[_]] = managedCollections

  val initialized: Channel[Boolean] = Channel[Boolean]
  lazy val realTime: RealTime = new RealTime(this)

  private val initCalled = new AtomicBoolean(false)

  /**
    * Initializes this graph instance, database, and session creating the graph and collections if it doesn't already
    * exist (if createGraph and createCollections is true). This can be invoked multiple times without risk of duplicate
    * functionality.
    *
    * @param createGraph automatically creates the graph if it doesn't already exist if set to true. Defaults to true.
    * @param createCollections automatically creates the collections if they don't already exist if set to true.
    *                          Defaults to true.
    * @return true if the operation completed without error
    */
  def init(createGraph: Boolean = true,
           createCollections: Boolean = true): Future[Boolean] = if (initCalled.compareAndSet(false, true)) {
    var future = graphFuture.flatMap { graph =>
      graph.exists().flatMap {
        case Some(response) => Future.successful(!response.error)
        case None if createGraph => graph.create().map(!_.error)
        case None => Future.successful(true)
      }
    }
    if (createCollections) {
      collections.foreach { collection =>
        future = future.flatMap { b =>
          if (b) {
            collection.collection.exists().flatMap {
              case Some(_) => Future.successful(true)
              case None => {
                collection.create(waitForSync = true).map { response =>
                  !response.error
                }
              }
            }
          } else {
            Future.successful(false)
          }
        }
      }
    }
    future.foreach(initialized := _)
    future
  } else {
    Future.successful(true)
  }

  def vertex[T <: DocumentOption](name: String): VertexCollection[T] = macro Macros.vertex[T]
  def edge[T <: Edge with DocumentOption](name: String, links: (String, String)*): EdgeCollection[T] = macro Macros.edge[T]

  def polymorphic[T <: PolymorphicDocumentOption](name: String, types: PolymorphicType[T]*): PolymorphicVertexCollection[T] = new PolymorphicVertexCollection[T](this, name, types.toList)

  def polymorphic2[T <: PolymorphicDocumentOption, P1 <: T, P2 <: T](name: String): PolymorphicVertexCollection[T] = macro Macros.polymorphic2[T, P1, P2]
  def polymorphic3[T <: PolymorphicDocumentOption, P1 <: T, P2 <: T, P3 <: T](name: String): PolymorphicVertexCollection[T] = macro Macros.polymorphic3[T, P1, P2, P3]
  def polymorphic4[T <: PolymorphicDocumentOption, P1 <: T, P2 <: T, P3 <: T, P4 <: T](name: String): PolymorphicVertexCollection[T] = macro Macros.polymorphic4[T, P1, P2, P3, P4]

  def polymorphicType[T <: PolymorphicDocumentOption, P <: T](value: String): PolymorphicType[T] = macro Macros.polymorphicType[T, P]

  /**
    * Deletes the graph.
    *
    * @param dropCollections true if the collections should also be dropped. Defaults to true.
    * @return true if the operation completed successfully
    */
  def delete(dropCollections: Boolean = true): Future[Boolean] = graphFuture.flatMap { graph =>
    graph.delete(dropCollections).map(!_.error)
  }

  def cursor: ArangoCursor = instance.db.cursor
  def call[T](query: Query)(implicit decoder: Decoder[T]): Future[T] = instance.db.call[T](query)
  def first[T](query: Query)(implicit decoder: Decoder[T]): Future[Option[T]] = instance.db.first[T](query)
  def execute(query: Query): Future[Boolean] = instance.db.execute(query)
}