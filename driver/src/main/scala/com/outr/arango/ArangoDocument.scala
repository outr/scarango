package com.outr.arango

import com.outr.arango.api.{APIDocumentCollection, APIDocumentDocumentHandle}
import com.outr.arango.model.ArangoCode
import io.circe.Json
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoDocument(client: HttpClient, dbName: String, collectionName: String) {
  def create(document: Json,
             transactionId: Option[String] = None,
             waitForSync: Boolean = false,
             returnNew: Boolean = true,
             returnOld: Boolean = false,
             silent: Boolean = false,
             overwrite: Boolean = false)
            (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    APIDocumentCollection.post(
      client = c,
      collection = collectionName,
      body = document,
      waitForSync = Some(waitForSync),
      returnNew = Some(returnNew),
      returnOld = Some(returnOld),
      silent = Some(silent),
      overwrite = Some(overwrite)
    ).map { json =>
      json.asArray match {
        case Some(array) => array.toList.map(json => JsonUtil.fromJson[DocumentInsert](Id.update(json)))
        case None => List(JsonUtil.fromJson[DocumentInsert](Id.update(json)))
      }
    }
  }

  def insertOne[D](document: D,
                   transactionId: Option[String] = None,
                   waitForSync: Boolean = false,
                   returnNew: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[DocumentInsert] = {
    val json = serialization.toJson(document)
    create(json, transactionId, waitForSync, returnNew, returnOld, silent)(ec).map(_.head)
  }

  def upsertOne[D](document: D,
                   transactionId: Option[String] = None,
                   waitForSync: Boolean = false,
                   returnNew: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[DocumentInsert] = {
    val json = serialization.toJson(document)
    create(json, transactionId, waitForSync, returnNew, returnOld, silent, overwrite = true)(ec).map(_.head)
  }

  def insert[D](documents: List[D],
                transactionId: Option[String] = None,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[List[DocumentInsert]] = {
    val json = Json.arr(documents.map(serialization.toJson): _*)
    create(json, transactionId, waitForSync, returnNew, returnOld, silent)(ec)
  }

  def upsert[D](documents: List[D],
                transactionId: Option[String] = None,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext, serialization: Serialization[D]): Future[List[DocumentInsert]] = {
    val json = Json.arr(documents.map(serialization.toJson): _*)
    create(json, transactionId, waitForSync, returnNew, returnOld, silent, overwrite = true)(ec)
  }

  def get[D](id: Id[D], transactionId: Option[String] = None)(implicit ec: ExecutionContext, serialization: Serialization[D]): Future[Option[D]] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    APIDocumentDocumentHandle
      .get(c, collectionName, id._key)
      .map(serialization.fromJson)
      .map(Some.apply)
      .recover {
        case exc: ArangoException if exc.error.errorCode == ArangoCode.ArangoDocumentNotFound => None
      }
  }

  def deleteOne[D](id: Id[D],
                   transactionId: Option[String] = None,
                   waitForSync: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext): Future[Id[D]] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    APIDocumentDocumentHandle.delete(
      client = c,
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
                transactionId: Option[String] = None,
                waitForSync: Boolean = false,
                returnOld: Boolean = false,
                ignoreRevs: Boolean = true)
               (implicit ec: ExecutionContext): Future[Json] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    APIDocumentCollection.delete(
      client = c,
      body = JsonUtil.toJson(ids),
      collection = collectionName,
      waitForSync = Some(waitForSync),
      returnOld = Some(returnOld),
      ignoreRevs = Some(ignoreRevs)
    )
  }
}