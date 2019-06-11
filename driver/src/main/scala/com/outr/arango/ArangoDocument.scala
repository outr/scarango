package com.outr.arango

import com.outr.arango.api.APIDocumentCollection
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
}