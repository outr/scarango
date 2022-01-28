package com.outr.arango.upgrade

import cats.effect.IO
import cats.implicits._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core.CreateCollectionOptions
import com.outr.arango.view.View
import com.outr.arango.{DatabaseStore, Document, Graph}

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
    _ <- graph.views.map(verifyView).sequence
    _ <- graph.stores.map(verifyStore).sequence
  } yield {
    ()
  }

  private def verifyCollection(collection: DocumentCollection[_]): IO[Unit] = for {
    exists <- collection.arangoCollection.collection.exists()
    created <- if (exists) {
      // Nothing to do
      IO(true)
    } else {
      scribe.info(s"${collection.dbName}.${collection.name} collection doesn't exist. Creating...")
      val options = CreateCollectionOptions(
        `type` = Some(collection.`type`)
        // TODO: Support other collection options
      )
      collection.arangoCollection.collection.create(options).map(_ => true)
    }
    _ = assert(created, s"${collection.dbName}.${collection.name} collection was not created successfully")
    indexes = collection.model.indexes
    _ <- collection.arangoCollection.index.ensure(indexes)
  } yield {
    ()
  }

  private def verifyView(view: View): IO[Unit] = for {
    exists <- view.exists()
    created <- if (exists) {
      // Nothing to do
      IO(true)
    } else {
      scribe.info(s"${view.dbName}.${view.name} view doesn't exist. Creating...")
      view.create().map(_ => true)
    }
    _ = assert(created, s"${view.name} view was not created successfully")
  } yield {
    ()
  }

  private def verifyStore(store: DatabaseStore): IO[Unit] = for {
    exists <- store.collection.collection.exists()
    created <- if (exists) {
      // Already exists, nothing to do
      IO(true)
    } else {
      scribe.info(s"${store.collection._collection.name()} key-store doesn't exist. Creating...")
      store.collection.collection.create().map(_ => true)       // TODO: Supply options for creating DatabaseStore
    }
    _ = assert(created, s"${store.collection._collection.name()} key-store was not created successfully")
  } yield {
    ()
  }
}