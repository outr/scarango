package com.outr.arango

import com.outr.arango.rest.BulkInserted
import io.circe.{Decoder, Encoder}

import scala.concurrent.Future

class ArangoBulk(document: ArangoDocument) {
  def insert[T](documents: Seq[T],
                overwrite: Boolean = false,
                waitForSync: Boolean = false,
                onDuplicate: OnDuplicate = OnDuplicate.Error,
                complete: Boolean = true,
                details: Boolean = false)
               (implicit encoder: Encoder[T], decoder: Decoder[BulkInserted]): Future[BulkInserted] = {
    val params = Map(
      "collection" -> document.collection.collection,
      "type" -> "list",
      "overwrite" -> overwrite.toString,
      "waitForSync" -> waitForSync.toString,
      "onDuplicate" -> onDuplicate.value,
      "complete" -> complete.toString,
      "details" -> details.toString
    )
    document.collection.db.restful[Seq[T], BulkInserted]("import", documents, params, anchor = Some("json"))
  }
}