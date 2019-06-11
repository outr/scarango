package com.outr.arango

import com.outr.arango.rest.{ArangoUser, CreateDatabaseRequest, Result}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.youi.client.ErrorHandler
import io.youi.http.{HttpRequest, HttpResponse, Method}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoDB(val session: ArangoSession, val db: String) {
  protected[arango] def restful[Request, Response](name: String,
                                                   request: Request,
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: Option[ErrorHandler[Response]] = None,
                                                   method: Method = Method.Post,
                                                   anchor: Option[String] = None)
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.restful[Request, Response](Some(db), name, request, params, errorHandler, method, anchor)
  }

  protected[arango] def call[Response](name: String,
                                       method: Method,
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: Option[ErrorHandler[Response]] = None)
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    session.call[Response](Some(db), name, method, params, errorHandler)
  }

  def create(users: ArangoUser*): Future[Result[Boolean]] = {
    val request = CreateDatabaseRequest(db, users.toList)
    session.restful[CreateDatabaseRequest, Result[Boolean]](None, "database", request)
  }

  def drop(): Future[Result[Boolean]] = {
    session.call[Result[Boolean]](None, s"database/$db", Method.Delete)
  }

  def collection(name: String): ArangoCollection = new ArangoCollection(this, name)

  lazy val cursor: ArangoCursor = new ArangoCursor(this)
  lazy val replication: ArangoReplication = new ArangoReplication(this)

  /**
    * Convenience method that calls `cursor` expecting exactly one result back. An assertion error will fire if the
    * results contains more or less than one result.
    *
    * @param query the query to execute
    * @param decoder decoder for T
    * @tparam T the type of the result
    * @return Future[T]
    */
  def call[T](query: Query)(implicit decoder: Decoder[T]): Future[T] = {
    cursor[T](query, count = true).map { response =>
      assert(response.count.contains(1), s"Response did not include exactly one result: ${response.count}.")
      response.result.head
    }
  }

  /**
    * Convenience method that calls `cursor` grabbing the first result returning None if there are no results.
    *
    * @param query the query to execute
    * @param decoder decoder for T
    * @tparam T the type of the result
    * @return optional T if there is at least one result
    */
  def first[T](query: Query)(implicit decoder: Decoder[T]): Future[Option[T]] = {
    cursor[T](query, batchSize = Some(1)).map(_.result.headOption)
  }

  /**
    * Convenience method that calls `cursor` expecting no results. An assertion error will be occur if the results count
    * is not exactly zero.
    *
    * @param query the query to execute
    * @return true if the query returned with no errors
    */
  def execute(query: Query): Future[Boolean] = {
    cursor[Unit](query, count = true).map { response =>
      assert(response.count.contains(0), s"Response count was not zero: ${response.count}.")
      !response.error
    }
  }

  lazy val graph: ArangoGraphs = new ArangoGraphs(this)
}