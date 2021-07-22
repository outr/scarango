package com.outr.arango

import com.outr.arango.api.{APIDocumentCollection, APIDocumentDocumentHandle}
import com.outr.arango.model.ArangoCode
import fabric._
import fabric.rw._
import io.youi.client.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class ArangoDocument(client: HttpClient, dbName: String, collectionName: String) {
  def create(document: fabric.Value,
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
      if (json.isArr) {
        json.asArr.value.toList.map(Id.update).map(_.as[DocumentInsert])
      } else {
        List(Id.update(json).as[DocumentInsert])
      }
    }
  }

  def insertOne[D: Reader](document: D,
                   transactionId: Option[String] = None,
                   waitForSync: Boolean = false,
                   returnNew: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    val json = document.toValue
    create(json, transactionId, waitForSync, returnNew, returnOld, silent)(ec).map(_.head)
  }

  def upsertOne[D: Reader](document: D,
                   transactionId: Option[String] = None,
                   waitForSync: Boolean = false,
                   returnNew: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    val json = document.toValue
    create(json, transactionId, waitForSync, returnNew, returnOld, silent, overwrite = true)(ec).map(_.head)
  }

  def insert[D: Reader](documents: List[D],
                transactionId: Option[String] = None,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    val json = arr(documents.map(_.toValue): _*)
    create(json, transactionId, waitForSync, returnNew, returnOld, silent)(ec)
  }

  def upsert[D: Reader](documents: List[D],
                transactionId: Option[String] = None,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    val json = arr(documents.map(_.toValue): _*)
    create(json, transactionId, waitForSync, returnNew, returnOld, silent, overwrite = true)(ec)
  }

  def get[D: Writer](id: Id[D], transactionId: Option[String] = None)(implicit ec: ExecutionContext): Future[Option[D]] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    APIDocumentDocumentHandle
      .get(c, collectionName, id._key)
      .map(_.as[D])
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
               (implicit ec: ExecutionContext): Future[List[Id[D]]] = {
    val c = transactionId match {
      case Some(tId) => client.header("x-arango-trx-id", tId)
      case None => client
    }
    APIDocumentCollection.delete(
      client = c,
      body = ids.toValue,
      collection = collectionName,
      waitForSync = Some(waitForSync),
      returnOld = Some(returnOld),
      ignoreRevs = Some(ignoreRevs)
    ).map { json =>
      json.asArr.value.toList.map(Id.extract[D])
    }
  }
}