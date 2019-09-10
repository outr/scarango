package com.outr.arango

import com.outr.arango.transaction.Transaction

import scala.concurrent.{ExecutionContext, Future}

class Collection[D <: Document[D]](val graph: Graph,
                                   val model: DocumentModel[D],
                                   val `type`: CollectionType,
                                   val indexes: List[Index],
                                   val transaction: Option[Transaction]) {
  lazy val arangoCollection: ArangoCollection = graph.arangoDatabase.collection(name)

  graph.add(this)

  def name: String = model.collectionName

  private def transactionId: Option[String] = transaction.map(_.id)

  def apply(transaction: Transaction): Collection[D] = new Collection[D](graph, model, `type`, indexes, Some(transaction))

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

  def get(id: Id[D])(implicit ec: ExecutionContext): Future[Option[D]] = {
    arangoCollection.document.get(id, transactionId)(ec, model.serialization)
  }

  def apply(id: Id[D])(implicit ec: ExecutionContext): Future[D] = {
    get(id).map(_.getOrElse(throw new RuntimeException(s"Unable to find $name by id: $id")))
  }

  def query(query: Query): QueryBuilder[D] = graph.query(query, transaction).as[D](model.serialization)

  lazy val all: QueryBuilder[D] = graph.query(Query(s"FOR c IN $name RETURN c", Map.empty), transaction).as[D](model.serialization)

  def deleteOne(id: Id[D],
                waitForSync: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[Id[D]] = {
    arangoCollection.document.deleteOne(id, transactionId, waitForSync, returnOld, silent)
  }

  protected[arango] def create(createCollection: Boolean)(implicit ec: ExecutionContext): Future[Unit] = for {
    // Create the collection if it doesn't already exist
    _ <- if (createCollection) {
      arangoCollection.create(`type` = `type`)
    } else {
      Future.successful(())
    }
    // List existing indexes
    existingIndexes <- if (createCollection) {
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

  def truncate()(implicit ec: ExecutionContext): Future[Unit] = arangoCollection.truncate().map { r =>
    if (r.error) {
      throw new RuntimeException(s"Error attempting to truncate $name")
    }
    ()
  }
}