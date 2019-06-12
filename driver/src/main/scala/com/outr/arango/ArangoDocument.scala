package com.outr.arango

import com.outr.arango.api.{APIDocumentCollection, APIDocumentDocumentHandle}
import io.circe.Json
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoDocument(client: HttpClient, dbName: String, collectionName: String) {
  def create(document: Json,
             waitForSync: Boolean = false,
             returnNew: Boolean = true,
             returnOld: Boolean = false,
             silent: Boolean = false,
             overwrite: Boolean = false)
            (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = APIDocumentCollection.post(
    client = client,
    collection = collectionName,
    body = document,
    waitForSync = Some(waitForSync),
    returnNew = Some(returnNew),
    returnOld = Some(returnOld),
    silent = Some(silent),
    overwrite = Some(overwrite)
  ).map { json =>
    json.asArray match {
      case Some(array) => array.toList.map(JsonUtil.fromJson[DocumentInsert](_))
      case None => List(JsonUtil.fromJson[DocumentInsert](json))
    }
  }

  def deleteOne[D](id: Id[D],
                   waitForSync: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext): Future[Json] = {
    APIDocumentDocumentHandle.delete(
      client = client,
      collectionName = id.collection,
      documentHandle = id.value,
      waitForSync = Some(waitForSync),
      returnOld = Some(returnOld),
      silent = Some(silent),
      IfMatch = None
    )
  }

  def delete[D](ids: List[Id[D]],
                waitForSync: Boolean = false,
                returnOld: Boolean = false,
                ignoreRevs: Boolean = true)
               (implicit ec: ExecutionContext): Future[Json] = {
    val body = Json.arr(ids.map { id =>
      Json.obj("_key" -> Json.fromString(id._key))
    }: _*)
    scribe.info(s"Keys: $body")
    APIDocumentCollection.delete(
      client = client,
      body = body,
      collection = collectionName,
      waitForSync = Some(waitForSync),
      returnOld = Some(returnOld),
      ignoreRevs = Some(ignoreRevs)
    )
  }
}