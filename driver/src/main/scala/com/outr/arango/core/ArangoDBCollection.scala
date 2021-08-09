package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.arangodb.entity.IndexEntity
import com.arangodb.model._
import com.outr.arango.util.Helpers._
import com.outr.arango.{Field, Index, IndexInfo, IndexType}

import scala.jdk.CollectionConverters._
import cats.implicits._

class ArangoDBCollection(collection: ArangoCollectionAsync) {
  def create(options: CreateCollectionOptions = CreateCollectionOptions()): IO[CollectionInfo] = {
    val o = options
    collection.create(new CollectionCreateOptions {
      name(collection.name())
      o.journalSize.foreach(journalSize(_))
      o.replicationFactor.foreach(replicationFactor(_))
      o.satellite.foreach(satellite(_))
      o.minReplicationFactor.foreach(minReplicationFactor(_))
      o.keyOptions.foreach(k => keyOptions(k.allowUserKeys, k.`type`, k.increment, k.offset))
    }).toIO.map(collectionEntityConversion)
  }

  def exists(): IO[Boolean] = collection.exists().toIO.map(_.booleanValue())

  def drop(): IO[Unit] = collection.drop().toIO.map(_ => ())

  def info(): IO[CollectionInfo] = collection.getInfo.toIO.map(collectionEntityConversion)

  object document {
    def insert(doc: fabric.Obj, options: CreateOptions = CreateOptions.Insert): IO[CreateResult] = collection
      .insertDocument(fabric.parse.Json.format(doc), options)
      .toIO
      .map(createDocumentEntityConversion)

    def upsert(doc: fabric.Obj, options: CreateOptions = CreateOptions.Upsert): IO[CreateResult] = insert(doc, options)

    def update(key: String, doc: fabric.Obj, options: UpdateOptions): IO[UpdateResult] = collection
      .updateDocument(key, fabric.parse.Json.format(doc), options)
      .toIO
      .map(updateDocumentEntityConversion)

    def delete(key: String, options: DeleteOptions = DeleteOptions.Default): IO[DeleteResult] = collection
      .deleteDocument(key, classOf[String], options)
      .toIO
      .map(deleteDocumentEntityConversion)

    object batch {
      def insert(docs: List[fabric.Obj], options: CreateOptions = CreateOptions.Insert): IO[CreateResults] = collection
        .insertDocuments(docs.map(fabric.parse.Json.format).asJava, options)
        .toIO
        .map(multiDocumentCreateConversion)

      def upsert(docs: List[fabric.Obj], options: CreateOptions = CreateOptions.Upsert): IO[CreateResults] = insert(docs, options)

      def delete(docs: List[fabric.Obj], options: DeleteOptions = DeleteOptions.Default): IO[DeleteResults] = collection
        .deleteDocuments(docs.map(fabric.parse.Json.format).asJava, classOf[String], options)
        .toIO
        .map(multiDocumentDeleteConversion)
    }
  }

  object field {
    def apply[F](name: String): Field[F] = Field[F](name)
  }

  object index {
    def query(): IO[List[IndexInfo]] = {
      collection.getIndexes.toIO.map(_.asScala.toList).map(_.map(indexEntityConversion))
    }

    def ensure(indexes: List[Index]): IO[List[IndexInfo]] = {
      val generate: List[IO[IndexEntity]] = indexes.map { i =>
        val fields = i.fields.asJava
        i.`type` match {
          case IndexType.Persistent => {
            val options = new PersistentIndexOptions
            options.sparse(i.sparse)
            options.unique(i.unique)
            options.estimates(i.estimates)
            collection.ensurePersistentIndex(fields, options).toIO
          }
          case IndexType.Geo => {
            val options = new GeoIndexOptions
            options.geoJson(i.geoJson)
            collection.ensureGeoIndex(fields, options).toIO
          }
          case IndexType.FullText => {
            val options = new FulltextIndexOptions
            options.minLength(i.minLength.toInt)
            collection.ensureFulltextIndex(fields, options).toIO
          }
          case IndexType.TTL => {
            val options = new TtlIndexOptions
            options.expireAfter(i.expireAfterSeconds)
            collection.ensureTtlIndex(fields, options).toIO
          }
        }
      }
      generate.map(_.map(indexEntityConversion)).sequence
    }

    def delete(indexIds: List[String]): IO[List[String]] = {
      indexIds.map { indexId =>
        collection.deleteIndex(indexId).toIO
      }.sequence
    }
  }
}
