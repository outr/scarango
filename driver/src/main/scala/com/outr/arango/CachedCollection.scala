package com.outr.arango

import com.outr.arango.query.Filter
import com.outr.arango.transaction.Transaction
import reactify._

import scala.concurrent.{ExecutionContext, Future}

class CachedCollection[D <: Document[D]](graph: Graph,
                                         model: DocumentModel[D],
                                         `type`: CollectionType,
                                         indexes: List[Index],
                                         transaction: Option[Transaction]) extends Collection[D](graph, model, `type`, indexes, transaction) { self =>
  private object _cache extends Var[Map[Id[D], D]](Map.empty) {
    def +=(tuple: (Id[D], D)): Unit = set(get + tuple)
  }
  def cache: Val[Map[Id[D], D]] = _cache

  private var future: Future[Unit] = Future.successful(())

  def refresh()(implicit ec: ExecutionContext): Future[Unit] = {
    all.batchSize(Int.MaxValue).results.map { list =>
      self.synchronized {
        _cache @= list.map(d => d._id -> d).toMap
      }
    }
  }

  override def insertOne(document: D,
                         waitForSync: Boolean,
                         returnNew: Boolean,
                         returnOld: Boolean,
                         silent: Boolean)
                        (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    self.synchronized {
      _cache += document._id -> document
    }
    super.insertOne(document, waitForSync, returnNew, returnOld, silent)
  }

  override def upsertOne(document: D, waitForSync: Boolean, returnNew: Boolean, returnOld: Boolean, silent: Boolean)(implicit ec: ExecutionContext): Future[DocumentInsert] = super.upsertOne(document, waitForSync, returnNew, returnOld, silent)

  override def insert(documents: List[D], waitForSync: Boolean, returnNew: Boolean, returnOld: Boolean, silent: Boolean)(implicit ec: ExecutionContext): Future[List[DocumentInsert]] = super.insert(documents, waitForSync, returnNew, returnOld, silent)

  override def upsert(documents: List[D], waitForSync: Boolean, returnNew: Boolean, returnOld: Boolean, silent: Boolean)(implicit ec: ExecutionContext): Future[List[DocumentInsert]] = super.upsert(documents, waitForSync, returnNew, returnOld, silent)

  override def update(filter: => Filter, fieldAndValues: FieldAndValue[_]*)(implicit ec: ExecutionContext): Future[Long] = super.update(filter, fieldAndValues)

  override def updateAll(fieldAndValues: FieldAndValue[_]*)(implicit ec: ExecutionContext): Future[Long] = super.updateAll(fieldAndValues)

  override def batch(iterator: Iterator[D], batchSize: Int, upsert: Boolean, waitForSync: Boolean, counter: Int)(implicit ec: ExecutionContext): Future[Int] = super.batch(iterator, batchSize, upsert, waitForSync, counter)

  override def get(id: Id[D])(implicit ec: ExecutionContext): Future[Option[D]] = super.get(id)

  override def apply(id: Id[D])(implicit ec: ExecutionContext): Future[D] = super.apply(id)

  override def query(query: Query): QueryBuilder[D] = super.query(query)

  override lazy val all: QueryBuilder[D] = ???

  override def deleteOne(id: Id[D], waitForSync: Boolean, returnOld: Boolean, silent: Boolean)(implicit ec: ExecutionContext): Future[Id[D]] = super.deleteOne(id, waitForSync, returnOld, silent)

  override def delete(ids: List[Id[D]], transactionId: Option[String], waitForSync: Boolean, returnOld: Boolean, ignoreRevs: Boolean)(implicit ec: ExecutionContext): Future[List[Id[D]]] = super.delete(ids, transactionId, waitForSync, returnOld, ignoreRevs)

  override def truncate()(implicit ec: ExecutionContext): Future[Unit] = super.truncate()
}
