package com.outr.arango.core

import cats.effect.IO
import cats.implicits._
import com.arangodb
import com.arangodb.entity.IndexEntity
import com.arangodb.model._
import com.outr.arango.mutation.DataMutation
import com.outr.arango.util.Helpers._
import com.outr.arango.{Field, Index, IndexInfo, IndexType}
import fabric.Json
import fabric.rw.RW

import scala.jdk.CollectionConverters._

class ArangoDBCollection(val _collection: arangodb.ArangoCollection) extends ArangoDBDocuments[fabric.Json] {
  def name: String = _collection.name()

  override def toT(value: Json): Json = value

  override def fromT(t: Json): Json = t

  object collection {
    def create(options: CreateCollectionOptions = CreateCollectionOptions()): IO[CollectionInfo] = {
      io(_collection.create(new ArangoDBCollectionCreateOptions(_collection.name(), options).arango)).map(collectionEntityConversion)
    }
    def exists(): IO[Boolean] = io(_collection.exists())
    def drop(): IO[Unit] = io(_collection.drop())
    def info(): IO[CollectionInfo] = io(_collection.getInfo).map(collectionEntityConversion)
    def truncate(): IO[CollectionInfo] = io(_collection.truncate()).map(collectionEntityConversion)
  }

  object field {
    def apply[F: RW](name: String, mutation: Option[DataMutation] = None): Field[F] =
      new Field[F](
        fieldName = name,
        container = false,
        mutation = mutation)(implicitly[RW[F]], None)
  }

  object index {
    def query(): IO[List[IndexInfo]] = {
      io(_collection.getIndexes).map(_.asScala.toList).map(_.map(indexEntityConversion))
    }

    def ensure(indexes: List[Index]): IO[List[IndexInfo]] = {
      val generate: List[IO[IndexEntity]] = indexes.map { i =>
        val fields = i.fields.asJava
        i.`type` match {
          case IndexType.Persistent => io {
            val options = new PersistentIndexOptions
            options.sparse(i.sparse)
            options.unique(i.unique)
            options.estimates(i.estimates)
            _collection.ensurePersistentIndex(fields, options)
          }
          case IndexType.Geo => io {
            val options = new GeoIndexOptions
            options.geoJson(i.geoJson)
            _collection.ensureGeoIndex(fields, options)
          }
          case IndexType.TTL => io {
            val options = new TtlIndexOptions
            options.expireAfter(i.expireAfterSeconds)
            _collection.ensureTtlIndex(fields, options)
          }
        }
      }
      generate.map(_.map(indexEntityConversion)).sequence
    }

    def delete(indexIds: List[String]): IO[List[String]] = {
      indexIds.map { indexId =>
        io(_collection.deleteIndex(indexId))
      }.sequence
    }
  }
}