package com.outr.arango

import com.outr.arango.rest.{IndexDelete, IndexInfo, IndexList}
import io.youi.http.Method

import io.circe.generic.auto._

import scala.concurrent.Future

class ArangoIndexing(collection: ArangoCollection) {
  lazy val hash = new ArangoIndexType(collection, "hash")
  lazy val skipList = new ArangoIndexType(collection, "skiplist")
  lazy val persistent = new ArangoIndexType(collection, "persistent")
  lazy val geo = new ArangoIndexType(collection, "geo")
  lazy val fullText = new ArangoIndexType(collection, "fulltext")

  def get(key: String): Future[IndexInfo] = {
    collection.db.call[IndexInfo](s"index/${collection.collection}/$key", Method.Get)
  }

  def list(): Future[IndexList] = {
    collection.db.call[IndexList]("index", Method.Get, Map("collection" -> collection.collection))
  }

  def delete(key: String): Future[IndexDelete] = {
    collection.db.call[IndexDelete](s"index/${collection.collection}/$key", Method.Delete)
  }
}

class ArangoIndexType(collection: ArangoCollection, `type`: String) {
  def create(fields: List[String], unique: Boolean = false, sparse: Boolean = false): Future[IndexInfo] = {
    val info = IndexInfo(`type`, Some(fields), Some(unique), Some(sparse))
    collection.db.restful[IndexInfo, IndexInfo](s"index", info, Map("collection" -> collection.collection))
  }
}
