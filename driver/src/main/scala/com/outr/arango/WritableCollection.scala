package com.outr.arango

import com.outr.arango.transaction.Transaction

import scala.concurrent.{ExecutionContext, Future}

trait WritableCollection[D <: Document[D]] extends Collection[D] {
  override def withTransaction(transaction: Transaction): WritableCollection[D] = new TransactionCollection[D](this, transaction, replicationFactor) with WritableCollection[D]

  def insertOne(document: D,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    arangoCollection.document.insertOne(document, transactionId, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def upsertOne(document: D,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    arangoCollection.document.upsertOne(document, transactionId, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def insert(documents: List[D],
             waitForSync: Boolean = false,
             returnNew: Boolean = false,
             returnOld: Boolean = false,
             silent: Boolean = false)
            (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    arangoCollection.document.insert(documents, transactionId, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def upsert(documents: List[D],
             waitForSync: Boolean = false,
             returnNew: Boolean = false,
             returnOld: Boolean = false,
             silent: Boolean = false)
            (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    arangoCollection.document.upsert(documents, transactionId, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def batch(iterator: Iterator[D],
            batchSize: Int = 5000,
            upsert: Boolean = false,
            waitForSync: Boolean = false,
            counter: Int = 0)
           (implicit ec: ExecutionContext): Future[Int] = {
    val documents = iterator.take(batchSize).toList
    val size = documents.length
    if (size == 0) {
      Future.successful(counter)
    } else {
      val future = if (upsert) {
        this.upsert(documents, waitForSync, silent = true)
      } else {
        this.insert(documents, waitForSync, silent = true)
      }
      future.flatMap { _ =>
        batch(iterator, batchSize, upsert, waitForSync, counter + size)
      }
    }
  }

  def deleteOne(id: Id[D],
                waitForSync: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[Id[D]] = {
    arangoCollection.document.deleteOne(id, transactionId, waitForSync, returnOld, silent)
  }

  def delete(ids: List[Id[D]],
             transactionId: Option[String] = None,
             waitForSync: Boolean = false,
             returnOld: Boolean = false,
             ignoreRevs: Boolean = true)
            (implicit ec: ExecutionContext): Future[List[Id[D]]] = {
    arangoCollection.document.delete(ids, transactionId, waitForSync, returnOld)
  }

  def truncate()(implicit ec: ExecutionContext): Future[Unit] = arangoCollection.truncate().map { r =>
    if (r.error) {
      throw new RuntimeException(s"Error attempting to truncate $name")
    }
    ()
  }
}