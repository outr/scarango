package com.outr.arango

import com.outr.arango.rest.CreateDocument
import io.circe.{Decoder, Encoder}
import io.youi.http.Method

import scala.concurrent.Future

class ArangoDocument(collection: ArangoCollection) {
  def byHandle[T](documentHandle: String)
                 (implicit decoder: Decoder[T]): Future[T] = {
    collection.db.call[T](s"document/${collection.collection}/$documentHandle", Method.Get)
  }

  def create[T](document: T,
                waitForSync: Option[Boolean] = None,
                returnNew: Boolean = false,
                silent: Boolean = false)
               (implicit encoder: Encoder[T], decoder: Decoder[CreateDocument[T]]): Future[CreateDocument[T]] = {
    collection.db.restful[T, CreateDocument[T]](s"document/${collection.collection}", document, params = List(
      waitForSync.map("waitForSync" -> _.toString),
      Some("returnNew" -> returnNew.toString),
      Some("silent" -> silent.toString)
    ).flatten.toMap)
  }
}