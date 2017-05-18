package com.outr.arango.managed

import java.util.concurrent.atomic.AtomicBoolean

import com.outr.arango.rest.CreateDatabaseResponse
import com.outr.arango.{Arango, ArangoCode, ArangoCursor, ArangoDB, ArangoGraph, ArangoSession, Credentials, DocumentOption, Edge, Macros, Query, ReplicationMonitor}
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
  private[managed] lazy val systemDbFuture: Future[ArangoDB] = sessionFuture.map(_.db("_system"))
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
    * @param createDatabase attempts to create the database, if it does not exist and user has the rights. Halts creation of
    *                       graph and collections on failure naturally
    * @return true if the operation completed without error
    */
  def init(createGraph: Boolean = true,
           createCollections: Boolean = true,
           createDatabase: Boolean = false ): Future[Boolean] = if (initCalled.compareAndSet(false, true)) {
    initDb( createDatabase, db ).flatMap {
      dbInitialized => if( dbInitialized) {
        initGraph(createGraph).flatMap {
          case true => initCollections(createCollections)
          case _ => Future.successful(false)
        }
      } else {
        Future.successful(false)
      }
    }
  } else {
    Future.successful(true)
  }

  private def initCollections(createCollections: Boolean): Future[Boolean] = {
    if (createCollections) {
      val results: List[Future[Boolean]] = collections.map { collection =>
        collection.collection.exists().flatMap {
          case Some(_) => Future.successful(true)
          case None => {
            collection.create(waitForSync = true).map { response =>
              !response.error
            }
          }
        }
      }
      // Fail fast, as soon as a single collection or graph fails
      Future.foldLeft(results)( true ) { (o, i) => i && o }
    } else {
      Future.successful(true)
    }
  }

  private def initGraph(createGraph: Boolean): Future[Boolean] = {
    graphFuture.flatMap { graph =>
      graph.exists().flatMap {
        case Some(response) => Future.successful(!response.error)
        case None if createGraph => graph.create().map(!_.error)
        case None => Future.successful(true)
      }
    }
  }

  private def initDb( createDatabase: Boolean, db: String ): Future[Boolean] = {
    if( createDatabase ) {
      systemDbFuture.flatMap { sysDb =>
        (for {
          dbExists <- sysDb.databaseExists(db)
          response <- if(dbExists) {
            Future.successful(CreateDatabaseResponse(false, true, ArangoCode.ArangoDuplicateName.code))
          } else {
            sysDb.createDatabase(db)
          }
        } yield response).flatMap {
          case CreateDatabaseResponse(_, error, _) => Future.successful(!error)
        }
      }
    } else {
      Future.successful(true)
    }
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

  def synchronous[T](future: Future[T], timeout: FiniteDuration = 10.seconds): T = try {
    Await.result(future, timeout)
  } catch {
    case t: Throwable => throw new RuntimeException("Error while executing asynchronously", t)
  }
}