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

  private var refreshed: Boolean = false
  private var refreshing: Future[Unit] = Future.successful(())

  def refresh()(implicit ec: ExecutionContext): Future[Unit] = if (refreshing.isCompleted) {
    refreshing = all.batchSize(Int.MaxValue).results.map { list =>
      self.synchronized {
        _cache @= list.map(d => d._id -> d).toMap
        refreshed = true
      }
    }
    refreshing
  } else {
    refreshing
  }

  private def verify()(implicit ec: ExecutionContext): Future[Unit] = if (refreshed) {
    Future.successful(())
  } else if (refreshing.isCompleted) {
    refresh()
  } else {
    refreshing
  }

  private def verified[Return](f: => Future[Return])
                              (implicit ec: ExecutionContext): Future[Return] = verify().flatMap(_ => f)

  private def insertCache(document: D): Unit = self.synchronized {
    if (_cache().contains(document._id)) throw new RuntimeException(s"${document._id} already exists")
    _cache += document._id -> document
  }

  private def updateCache(document: D): Unit = self.synchronized {
    _cache += document._id -> document
  }

  override def insertOne(document: D,
                         waitForSync: Boolean,
                         returnNew: Boolean,
                         returnOld: Boolean,
                         silent: Boolean)
                        (implicit ec: ExecutionContext): Future[DocumentInsert] = verified {
    insertCache(document)
    super.insertOne(document, waitForSync, returnNew, returnOld, silent)
    Future.successful(DocumentInsert(None, `new` = None, old = None))
  }

  override def upsertOne(document: D, waitForSync: Boolean, returnNew: Boolean, returnOld: Boolean, silent: Boolean)
                        (implicit ec: ExecutionContext): Future[DocumentInsert] = verified {
    updateCache(document)
    super.upsertOne(document, waitForSync, returnNew, returnOld, silent)
    Future.successful(DocumentInsert(None, `new` = None, old = None))
  }

  override def insert(documents: List[D], waitForSync: Boolean, returnNew: Boolean, returnOld: Boolean, silent: Boolean)
                     (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = verified {
    self.synchronized {
      documents.foreach(insertCache)
    }
    super.insert(documents, waitForSync, returnNew, returnOld, silent)
    val insert = DocumentInsert(None, `new` = None, old = None)
    Future.successful(documents.map(_ => insert))
  }

  override def upsert(documents: List[D], waitForSync: Boolean, returnNew: Boolean, returnOld: Boolean, silent: Boolean)
                     (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = verified {
    self.synchronized {
      documents.foreach(updateCache)
    }
    super.upsert(documents, waitForSync, returnNew, returnOld, silent)
    val insert = DocumentInsert(None, `new` = None, old = None)
    Future.successful(documents.map(_ => insert))
  }

  override def update(filter: => Filter, fieldAndValues: FieldAndValue[_]*)
                     (implicit ec: ExecutionContext): Future[Long] = throw new UnsupportedOperationException

  override def updateAll(fieldAndValues: FieldAndValue[_]*)
                        (implicit ec: ExecutionContext): Future[Long] = throw new UnsupportedOperationException

  override def batch(iterator: Iterator[D], batchSize: Int, upsert: Boolean, waitForSync: Boolean, counter: Int)
                    (implicit ec: ExecutionContext): Future[Int] = throw new UnsupportedOperationException

  override def get(id: Id[D])(implicit ec: ExecutionContext): Future[Option[D]] = verified {
    Future.successful(cache().get(id))
  }

  override def apply(id: Id[D])(implicit ec: ExecutionContext): Future[D] = verified {
    Future.successful(cache()(id))
  }

  override def query(query: Query): QueryBuilder[D] = throw new UnsupportedOperationException

  override lazy val all: QueryBuilder[D] = throw new UnsupportedOperationException

  override def deleteOne(id: Id[D], waitForSync: Boolean, returnOld: Boolean, silent: Boolean)
                        (implicit ec: ExecutionContext): Future[Id[D]] = verified {
    self.synchronized {
      _cache @= _cache - id
    }
    super.deleteOne(id, waitForSync, returnOld, silent)
    Future.successful(id)
  }

  override def delete(ids: List[Id[D]],
                      transactionId: Option[String],
                      waitForSync: Boolean,
                      returnOld: Boolean,
                      ignoreRevs: Boolean)
                     (implicit ec: ExecutionContext): Future[List[Id[D]]] = verified {
    self.synchronized {
      ids.foreach(_cache @= _cache - _)
    }
    super.delete(ids, transactionId, waitForSync, returnOld, ignoreRevs)
    Future.successful(ids)
  }

  override def truncate()(implicit ec: ExecutionContext): Future[Unit] = verified {
    self.synchronized {
      _cache @= Map.empty
    }
    super.truncate()
    Future.successful(())
  }
}
