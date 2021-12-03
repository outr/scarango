package com.outr.arango.upgrade

import cats.effect.IO
import cats.implicits._
import com.outr.arango.core.CreateCollectionOptions
import com.outr.arango.{Collection, DatabaseStore, Document, DocumentCollection, Graph}

object CreateDatabase extends DatabaseUpgrade {
  override def applyToNew: Boolean = true
  override def blockStartup: Boolean = true
  override def alwaysRun: Boolean = true

  override def upgrade(graph: Graph): IO[Unit] = for {
    databaseExists <- graph.db.exists()
    databaseCreated <- if (databaseExists) {
      // Database already exists
      IO(true)
    } else {
      scribe.info(s"${graph.databaseName} doesn't exist. Creating...")
      graph.db.create()
    }
    _ = assert(databaseCreated, s"${graph.databaseName} database was not created successfully")
    _ = scribe.info(s"Verifying ${graph.collections.length} collections (${graph.collections.map(_.name).mkString(", ")})...")
    _ <- graph.collections.map(verifyCollection).sequence
    _ <- graph.stores.map(verifyStore).sequence
  } yield {
    ()
  }

  private def verifyCollection(collection: DocumentCollection[_]): IO[Unit] = for {
    exists <- collection.collection.exists()
    created <- if (exists) {
      // Nothing to do
      IO(true)
    } else {
      scribe.info(s"${collection.dbName}.${collection.name} collection doesn't exist. Creating...")
      val options = CreateCollectionOptions(
        `type` = Some(collection.`type`)
      )
      collection.collection.create(options).map(_ => true)    // TODO: supply options
    }
    _ = assert(created, s"${collection.dbName}.${collection.name} collection was not created successfully")
    indexes = collection.model.indexes
    _ <- collection.collection.index.ensure(indexes)
    // TODO: Support views
  } yield {
    ()
  }

  private def verifyStore(store: DatabaseStore): IO[Unit] = for {
    exists <- store.collection.exists()
    created <- if (exists) {
      // Already exists, nothing to do
      IO(true)
    } else {
      scribe.info(s"${store.collection.collection.name()} key-store doesn't exist. Creating...")
      store.collection.create().map(_ => true)       // TODO: Supply options
    }
    _ = assert(created, s"${store.collection.collection.name()} key-store was not created successfully")
  } yield {
    ()
  }
}