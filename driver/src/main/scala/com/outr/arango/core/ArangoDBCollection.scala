package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.arangodb.entity.IndexEntity
import com.arangodb.model._
import com.outr.arango.util.Helpers._
import com.outr.arango.{Field, Index, IndexInfo, IndexType}

import scala.jdk.CollectionConverters._
import cats.implicits._
import fabric.Json
import fabric.rw.RW

class ArangoDBCollection(val _collection: ArangoCollectionAsync) extends ArangoDBDocuments[fabric.Json] {
  def name: String = _collection.name()

  override def toT(value: Json): Json = value

  override def fromT(t: Json): Json = t

  object collection {
    def create(options: CreateCollectionOptions = CreateCollectionOptions()): IO[CollectionInfo] = {
      _collection.create(new ArangoDBCollectionCreateOptions(_collection.name(), options)).toIO.map(collectionEntityConversion)
    }
    def exists(): IO[Boolean] = _collection.exists().toIO.map(_.booleanValue())
    def drop(): IO[Unit] = _collection.drop().toIO.map(_ => ())
    def info(): IO[CollectionInfo] = _collection.getInfo.toIO.map(collectionEntityConversion)
    def truncate(): IO[CollectionInfo] = _collection.truncate().toIO.map(collectionEntityConversion)
  }

  object field {
    def apply[F: RW](name: String): Field[F] =
      new Field[F](name, mutation = None)(implicitly[RW[F]], None)
  }

  object index {
    def query(): IO[List[IndexInfo]] = {
      _collection.getIndexes.toIO.map(_.asScala.toList).map(_.map(indexEntityConversion))
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
            _collection.ensurePersistentIndex(fields, options).toIO
          case IndexType.Geo =>
            val options = new GeoIndexOptions
            options.geoJson(i.geoJson)
            _collection.ensureGeoIndex(fields, options).toIO
          case IndexType.FullText =>
            val options = new FulltextIndexOptions
            options.minLength(i.minLength.toInt)
            _collection.ensureFulltextIndex(fields, options).toIO
          case IndexType.TTL =>
            val options = new TtlIndexOptions
            options.expireAfter(i.expireAfterSeconds)
            _collection.ensureTtlIndex(fields, options).toIO
        }
      }
      generate.map(_.map(indexEntityConversion)).sequence
    }

    def delete(indexIds: List[String]): IO[List[String]] = {
      indexIds.map { indexId =>
        _collection.deleteIndex(indexId).toIO
      }.sequence
    }
  }
}