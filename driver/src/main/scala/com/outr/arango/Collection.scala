package com.outr.arango

import com.outr.arango.transaction.Transaction

import scala.concurrent.{ExecutionContext, Future, Promise}

trait Collection[D <: Document[D]] {
  private[arango] var _id: String = _

  def id: String = _id
  lazy val arangoCollection: ArangoCollection = graph.arangoDatabase.collection(name)

  addCollection()

  def graph: Graph
  def model: DocumentModel[D]
  def `type`: CollectionType
  def indexes: List[Index]
  def transaction: Option[Transaction]
  def transactionId: Option[String] = transaction.map(_.id)

  def name: String = model.collectionName

  def withTransaction(transaction: Transaction): Collection[D] = new TransactionCollection[D](this, transaction)

  def get(id: Id[D])(implicit ec: ExecutionContext): Future[Option[D]] = {
    arangoCollection.document.get(id, transactionId)(ec, model.serialization)
  }

  def byIds(ids: Id[D]*)(implicit ec: ExecutionContext): Future[List[D]] = if (ids.nonEmpty) {
    graph.query(
      query = Query(s"FOR c IN $name FILTER c._id IN @ids RETURN c", Map("ids" -> Value.values(ids.map(Value.id)))),
      transaction = transaction
    ).as[D](model.serialization).batchSize(ids.length).results
  } else {
    Future.successful(Nil)
  }

  def apply(id: Id[D])(implicit ec: ExecutionContext): Future[D] = {
    get(id).map(_.getOrElse(throw new RuntimeException(s"Unable to find $name by id: $id")))
  }

  def monitor(monitor: WriteAheadLogMonitor): CollectionMonitor[D] = {
    val promise = Promise[Unit]()
    monitor.tailed.once(_ => promise.success(()))
    new CollectionMonitor[D](monitor, this, promise.future)
  }

  protected def addCollection(): Unit = graph.add(this)

  protected[arango] def create(collectionId: Option[String])(implicit ec: ExecutionContext): Future[Unit] = for {
    // Create the collection if it doesn't already exist
    _ <- if (collectionId.isEmpty) {
      arangoCollection.create(`type` = `type`).map { info =>
        info.id.foreach(id => _id = id)
      }
    } else {
      Future.successful(())
    }
    // Set the collection id
    _ = collectionId.foreach(id => _id = id)
    // List existing indexes
    existingIndexes <- if (collectionId.isEmpty) {
      Future.successful(Nil) // No indexes will exist if collection was just created, so don't waste the call
    } else {
      arangoCollection.index.list().map(_.indexes)
    }
    // Create new indexes
    newIndexes = indexes.filterNot(i => existingIndexes.exists(info => i.typeAndFields(info)))
    _ = if (newIndexes.nonEmpty) scribe.info(s"$name: Creating new indexes $newIndexes")
    _ <- Future.sequence(newIndexes.map(i => arangoCollection.index.create(i)))
    // TODO: Support replacing modified indexes
    // Delete old indexes
    deleteIndexes = existingIndexes.filterNot(info => indexes.exists(i => i.typeAndFields(info)) || info.fields.contains(List("_key")) || info.fields.contains(List("_from", "_to")))
    _ = if (deleteIndexes.nonEmpty) scribe.info(s"$name: Deleting old indexes $deleteIndexes")
    _ <- Future.sequence(deleteIndexes.map(i => arangoCollection.index.delete(i.id)))
  } yield {
    ()
  }
}