package com.outr.arango

import com.outr.arango.rest.{BulkInserted, CreateDocument}
import io.circe._
import io.circe.parser._
import io.circe.syntax._
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

  def upsert[T <: DocumentOption](document: T)
               (implicit encoder: Encoder[T], decoder: Decoder[T]): Future[T] = {
    val json = document.asJson
    val jsonString = Printer.spaces2.pretty(json)
    val queryString =
      s"""
         |UPSERT { _key: @key }
         |INSERT $jsonString
         |UPDATE $jsonString IN ${collection.collection}
         |RETURN NEW
       """.stripMargin
    val query = Query(queryString, Map("key" -> Value.string(document._key.get)))
    collection.db.call[T](query)
  }

  object bulk {
    def insert[T](documents: Seq[T],
                  overwrite: Boolean = false,
                  waitForSync: Boolean = false,
                  onDuplicate: String = "error",
                  complete: Boolean = true,
                  details: Boolean = false)
                 (implicit encoder: Encoder[T], decoder: Decoder[BulkInserted]): Future[BulkInserted] = {
      val params = Map(
        "collection" -> collection.collection,
        "type" -> "list",
        "overwrite" -> overwrite.toString,
        "waitForSync" -> waitForSync.toString,
        "onDuplicate" -> onDuplicate,
        "complete" -> complete.toString,
        "details" -> details.toString
      )
      collection.db.restful[Seq[T], BulkInserted]("import", documents, params, anchor = Some("json"))
    }
  }
}