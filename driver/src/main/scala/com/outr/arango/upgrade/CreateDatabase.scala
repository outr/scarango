package com.outr.arango.upgrade

import cats.effect.IO
import cats.implicits._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core.{CollectionSchema, CreateCollectionOptions}
import com.outr.arango.view.View
import com.outr.arango.{DatabaseStore, DocumentModel, Graph}

object CreateDatabase extends DatabaseUpgrade {
  override def applyToNew: Boolean = true
  override def blockStartup: Boolean = true
  override def alwaysRun: Boolean = true

  override def upgrade(graph: Graph): IO[Unit] = if (graph.managed) {
    for {
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
  } else {
    scribe.info(s"${graph.databaseName} is configured as unmanaged, skipping creation and verification.")
    IO.unit
  }

  private def verifyCollection(collection: DocumentCollection[_, _ <: DocumentModel[_]]): IO[Unit] = if (collection.managed) {
    for {
      exists <- collection.arangoCollection.collection.exists()
      created <- if (exists) {
        // Nothing to do
        IO(true)
      } else {
        scribe.info(s"${collection.dbName}.${collection.name} collection doesn't exist. Creating...")
        val options = CreateCollectionOptions(
          `type` = Some(collection.`type`),
          waitForSync = collection.model.waitForSync,
          computedValues = collection.model.allComputedValues,
          collectionSchema = collection.model.schema.getOrElse(CollectionSchema())
          // TODO: Support other collection options
        )
        collection.arangoCollection.collection.create(options).map(_ => true)
      }
      _ = assert(created, s"${collection.dbName}.${collection.name} collection was not created successfully")
      indexes = collection.model.indexes
      _ <- collection.arangoCollection.index.ensure(indexes)
      _ <- collection.arangoCollection.ensure(
        waitForSync = collection.model.waitForSync,
        schema = collection.model.schema,
        computedValues = collection.model.allComputedValues
      )
    } yield {
      ()
    }
  } else {
    scribe.info(s"${collection.name} is configured as unmanaged, skipping creation and verification.")
    IO.unit
  }

  private def verifyView(view: View): IO[Unit] = if (view.managed) {
    for {
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
  } else {
    scribe.info(s"${view.name} is configured as unmanaged, skipping creation and verification.")
    IO.unit
  }

  private def verifyStore(store: DatabaseStore): IO[Unit] = if (store.managed) {
    for {
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
  } else {
    scribe.info(s"${store.collection.name} is configured as unmanaged, skipping creation and verification.")
    IO.unit
  }
}