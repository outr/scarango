package com.outr.arango

import reactify._

import scala.concurrent.{ExecutionContext, Future}

class CachedCollection[D <: Document[D]](override val collection: Collection[D], val replicationFactor: Long) extends WrappedCollection[D] with WritableCollection[D] { self =>
  private object _cache extends Var[Map[Id[D], D]](Map.empty) {
    def +=(tuple: (Id[D], D)): Unit = set(get + tuple)
  }
  def cachedMap: Val[Map[Id[D], D]] = _cache
  val removed: Channel[D] = Channel[D]
  val added: Channel[D] = Channel[D]
  val cache: Val[List[D]] = {
    val v = Val(cachedMap.values.toList)
    v.changes {
      case (previous, current) => {
        val removed = previous.diff(current)
        val added = current.diff(previous)
        removed.foreach(this.removed.static)
        added.foreach(this.added.static)
      }
    }
    v
  }

  def filter(f: D => Boolean): List[D] = cache().filter(f)

  def find(f: D => Boolean): Option[D] = cache().find(f)

  private var refreshed: Boolean = false
  private var refreshing: Future[Unit] = Future.successful(())

  def refresh()(implicit ec: ExecutionContext): Future[Unit] = if (refreshing.isCompleted) {
    val now = System.currentTimeMillis()
    val query = graph.query(Query(s"FOR c IN $name RETURN c", Map.empty), transaction).as[D](model.serialization)
    refreshing = query.batchSize(Int.MaxValue).results.map { list =>
      self.synchronized {
        _cache @= list.map(d => d._id -> d).toMap
        scribe.info(s"Refreshed $name collection cached data (${list.length} items) in ${(System.currentTimeMillis() - now) / 1000.0} seconds")
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

  def cached(id: Id[D]): Option[D] = {
    assert(refreshed, "Cannot be called until the collection has loaded for the first time")
    cachedMap().get(id)
  }

  def one(id: Id[D]): D = cached(id).getOrElse(throw new RuntimeException(s"$id does not exist"))

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

  override def batch(iterator: Iterator[D], batchSize: Int, upsert: Boolean, waitForSync: Boolean, counter: Int)
                    (implicit ec: ExecutionContext): Future[Int] = throw new UnsupportedOperationException

  override def get(id: Id[D])(implicit ec: ExecutionContext): Future[Option[D]] = verified {
    Future.successful(cachedMap().get(id))
  }

  override def apply(id: Id[D])(implicit ec: ExecutionContext): Future[D] = verified {
    Future.successful(cachedMap()(id))
  }

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
