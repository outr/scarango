package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.arangodb.entity.IndexEntity
import com.arangodb.model._
import com.outr.arango.util.Helpers._
import com.outr.arango.{Field, Index, IndexInfo, IndexType}

import scala.jdk.CollectionConverters._
import cats.implicits._

class ArangoDBCollection(val collection: ArangoCollectionAsync) {
 def name: String = collection.name()

  def create(options: CreateCollectionOptions = CreateCollectionOptions()): IO[CollectionInfo] = {
    val o = options
    collection.create(new CollectionCreateOptions {
      this.name(collection.name())
      o.`type`.foreach(t => `type`(t))
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

  def truncate(): IO[CollectionInfo] = collection.truncate().toIO.map(collectionEntityConversion)

  lazy val document = new ArangoDBDocuments[fabric.Value](collection, fabric.parse.Json.parse, fabric.parse.Json.format(_))

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
          case IndexType.Persistent =>
            val options = new PersistentIndexOptions
            options.sparse(i.sparse)
            options.unique(i.unique)
            options.estimates(i.estimates)
            collection.ensurePersistentIndex(fields, options).toIO
          case IndexType.Geo =>
            val options = new GeoIndexOptions
            options.geoJson(i.geoJson)
            collection.ensureGeoIndex(fields, options).toIO
          case IndexType.FullText =>
            val options = new FulltextIndexOptions
            options.minLength(i.minLength.toInt)
            collection.ensureFulltextIndex(fields, options).toIO
          case IndexType.TTL =>
            val options = new TtlIndexOptions
            options.expireAfter(i.expireAfterSeconds)
            collection.ensureTtlIndex(fields, options).toIO
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