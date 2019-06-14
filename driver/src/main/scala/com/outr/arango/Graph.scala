package com.outr.arango

import io.youi.client.HttpClient
import io.youi.net.URL

import scala.concurrent.{ExecutionContext, Future}

/**
  * Graph represents a graph database
  *
  * TODO: This is currently an anonymous graph without support for named graphs. Support should be added for named.
  */
class Graph(val database: String = ArangoDB.config.db,
            baseURL: URL = ArangoDB.config.url,
            credentials: Option[Credentials] = ArangoDB.credentials,
            httpClient: HttpClient = HttpClient) {
  private val arango: ArangoDB = new ArangoDB(database, baseURL, credentials, httpClient)
  private var _collections: List[Collection[_]] = Nil
  def collections: List[Collection[_]] = _collections

  def init()(implicit ec: ExecutionContext): Future[Unit] = arango.init().flatMap { _ =>
    // TODO: Support Upgrades, Indexes, and Key/Value Store
    Future.successful(())
  }

  private[arango] def add[D <: Document[D]](collection: Collection[D]): Unit = synchronized {
    _collections = _collections ::: List(collection)
  }
}