package com.outr.arango

import scala.concurrent.{ExecutionContext, Future}

class Collection[D <: Document[D]](val graph: Graph,
                                   val model: DocumentModel[D],
                                   val `type`: CollectionType,
                                   val indexes: List[Index]) {
  lazy val arangoCollection: ArangoCollection = graph.arangoDatabase.collection(name)

  graph.add(this)

  def name: String = model.collectionName

  def insertOne(document: D,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    arangoCollection.document.insertOne(document, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def upsertOne(document: D,
                waitForSync: Boolean = false,
                returnNew: Boolean = false,
                returnOld: Boolean = false,
                silent: Boolean = false)
               (implicit ec: ExecutionContext): Future[DocumentInsert] = {
    arangoCollection.document.upsertOne(document, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def insert(documents: List[D],
             waitForSync: Boolean = false,
             returnNew: Boolean = false,
             returnOld: Boolean = false,
             silent: Boolean = false)
            (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    arangoCollection.document.insert(documents, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def upsert(documents: List[D],
             waitForSync: Boolean = false,
             returnNew: Boolean = false,
             returnOld: Boolean = false,
             silent: Boolean = false)
            (implicit ec: ExecutionContext): Future[List[DocumentInsert]] = {
    arangoCollection.document.upsert(documents, waitForSync, returnNew, returnOld, silent)(ec, model.serialization)
  }

  def batch(iterator: Iterator[D],
            batchSize: Int = 1000,
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
    arangoCollection.document.get(id)(ec, model.serialization)
  }

  def query(query: Query): QueryBuilder[D] = graph.query(query).as[D](model.serialization)

  def deleteOne(id: Id[D],
                   waitForSync: Boolean = false,
                   returnOld: Boolean = false,
                   silent: Boolean = false)
                  (implicit ec: ExecutionContext): Future[Id[D]] = {
    arangoCollection.document.deleteOne(id, waitForSync, returnOld, silent)
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
      Future.successful(Nil)      // No indexes will exist if collection was just created, so don't waste the call
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
