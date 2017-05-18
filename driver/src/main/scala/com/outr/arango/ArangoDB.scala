package com.outr.arango

import com.outr.arango.rest.{ArangoUser, CreateDatabaseRequest, CreateDatabaseResponse, DatabaseListResponse}
import io.circe.{Decoder, Encoder}
import io.youi.http.{HttpRequest, HttpResponse, Method}
import io.circe.generic.auto._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoDB(val session: ArangoSession, val db: String) {
  protected[arango] def databaseExists(name: String): Future[Boolean] = {
    val databaseList = session.instance.call[DatabaseListResponse](
      s"/_db/$db/_api/database",
      Method.Get,
      session.token)

    databaseList.flatMap {
      r: DatabaseListResponse => Future.successful(
        r.result.contains(name)
      )
    }
  }

  protected[arango] def createDatabase( name: String): Future[CreateDatabaseResponse] = {
    val request = if( Arango.defaultAuthentication ) {
      CreateDatabaseRequest(name, List( ArangoUser(Arango.defaultUsername, Option(Arango.defaultPassword))))
    } else {
      CreateDatabaseRequest(name)
    }
    session.instance.restful[CreateDatabaseRequest, CreateDatabaseResponse](
      s"/_db/$db/_api/database",
      request,
      session.token
    )
  }

  protected[arango] def restful[Request, Response](name: String,
                                                   request: Request,
                                                   params: Map[String, String] = Map.empty,
                                                   errorHandler: Option[(HttpRequest, HttpResponse) => Response] = None,
                                                   method: Method = Method.Post)
                                                  (implicit encoder: Encoder[Request], decoder: Decoder[Response]): Future[Response] = {
    session.instance.restful[Request, Response](s"/_db/$db/_api/$name", request, session.token, params, errorHandler, method)
  }

  protected[arango] def call[Response](name: String,
                                       method: Method,
                                       params: Map[String, String] = Map.empty,
                                       errorHandler: Option[(HttpRequest, HttpResponse) => Response] = None)
                                      (implicit decoder: Decoder[Response]): Future[Response] = {
    session.instance.call[Response](s"/_db/$db/_api/$name", method, session.token, params, errorHandler)
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