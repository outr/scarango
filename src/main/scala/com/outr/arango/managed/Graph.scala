package com.outr.arango.managed

import com.outr.arango.{Arango, ArangoCursor, ArangoDB, ArangoGraph, ArangoSession, DocumentOption, Macros}
import io.youi.net.URL

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.experimental.macros

class Graph(name: String,
            db: String = "_system",
            url: URL = Arango.defaultURL,
            username: String = Arango.defaultUsername,
            password: String = Arango.defaultPassword,
            timeout: FiniteDuration = 15.seconds) {
  private[managed] lazy val arango: Arango = new Arango(url)
  private[managed] lazy val sessionFuture: Future[ArangoSession] = arango.auth(username, password)
  private[managed] lazy val dbFuture: Future[ArangoDB] = sessionFuture.map(_.db(db))
  private[managed] lazy val graphFuture: Future[ArangoGraph] = dbFuture.map(_.graph(name))

  private[managed] lazy val instance: ArangoGraph = Await.result[ArangoGraph](graphFuture, timeout)

  /**
    * Initializes this graph instance, database, and session creating the graph if it doesn't already exist.
    *
    * @param autoCreate automatically creates the graph if it doesn't already exist if set to true. Defaults to true.
    * @return true if the operation completed without error
    */
  def init(autoCreate: Boolean = true): Future[Boolean] = graphFuture.flatMap { graph =>
    graph.exists().flatMap {
      case Some(response) => Future.successful(!response.error)
      case None if autoCreate => graph.create().map(!_.error)
      case None => Future.successful(true)
    }
  }

  def collection[T <: DocumentOption](name: String): Collection[T] = macro Macros.collection[T]

  def polymorphic[T <: PolymorphicDocumentOption](name: String, types: PolymorphicType[T]*): PolymorphicCollection[T] = new PolymorphicCollection[T](this, name, types.toList)

  def polymorphic2[T <: PolymorphicDocumentOption, P1 <: T, P2 <: T](name: String): PolymorphicCollection[T] = macro Macros.polymorphic2[T, P1, P2]
  def polymorphic3[T <: PolymorphicDocumentOption, P1 <: T, P2 <: T, P3 <: T](name: String): PolymorphicCollection[T] = macro Macros.polymorphic3[T, P1, P2, P3]
  def polymorphic4[T <: PolymorphicDocumentOption, P1 <: T, P2 <: T, P3 <: T, P4 <: T](name: String): PolymorphicCollection[T] = macro Macros.polymorphic4[T, P1, P2, P3, P4]

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
}