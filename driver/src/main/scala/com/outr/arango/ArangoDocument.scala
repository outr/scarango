package com.outr.arango

import com.outr.arango.api.{APIDocumentCollection, APIDocumentDocumentHandle}
import com.outr.arango.model.ArangoCode
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
      case Some(array) => array.toList.map(json => JsonUtil.fromJson[DocumentInsert](Id.update(json, removeIdentity = false)))
      case None => List(JsonUtil.fromJson[DocumentInsert](Id.update(json, removeIdentity = false)))
    }
  }

  def insertOne[D](document: D,
                   waitForSync: Boolean = false,
                   returnNew: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[DocumentInsert] = {
    val json = serialization.toJson(document)
    create(json, waitForSync, returnNew, returnOld, silent)(ec).map(_.head)
  }

  def upsertOne[D](document: D,
                   waitForSync: Boolean = false,
                   returnNew: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[DocumentInsert] = {
    val json = serialization.toJson(document)
    create(json, waitForSync, returnNew, returnOld, silent, overwrite = true)(ec).map(_.head)
  }

  def insert[D](documents: List[D],
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[List[DocumentInsert]] = {
    val json = Json.arr(documents.map(serialization.toJson): _*)
    create(json, waitForSync, returnNew, returnOld, silent)(ec)
  }

  def upsert[D](documents: List[D],
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[List[DocumentInsert]] = {
    val json = Json.arr(documents.map(serialization.toJson): _*)
    create(json, waitForSync, returnNew, returnOld, silent, overwrite = true)(ec)
  }

  def get[D](id: Id[D])(implicit ec: ExecutionContext, serialization: Serialization[D]): Future[Option[D]] = {
    APIDocumentDocumentHandle
      .get(client, collectionName, id._key)
      .map(serialization.fromJson)
      .map(Some.apply)
      .recover {
        case exc: ArangoException if exc.error.errorCode == ArangoCode.ArangoDocumentNotFound => None
      }
  }

  def deleteOne[D](id: Id[D],
                   waitForSync: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext): Future[Id[D]] = {
    APIDocumentDocumentHandle.delete(
      client = client,
      collectionName = id.collection,
      documentHandle = id.value,
      waitForSync = Some(waitForSync),
      returnOld = Some(returnOld),
      silent = Some(silent),
      IfMatch = None
    ).map { json =>
      Id.extract[D](json)
    }
  }

  def delete[D](ids: List[Id[D]],
                waitForSync: Boolean = false,
                returnOld: Boolean = false,
                ignoreRevs: Boolean = true)
               (implicit ec: ExecutionContext): Future[Json] = {
    APIDocumentCollection.delete(
      client = client,
      body = JsonUtil.toJson(ids),
      collection = collectionName,
      waitForSync = Some(waitForSync),
      returnOld = Some(returnOld),
      ignoreRevs = Some(ignoreRevs)
    )
  }
}