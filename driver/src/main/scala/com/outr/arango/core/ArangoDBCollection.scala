package com.outr.arango.core

import cats.effect.IO
import cats.implicits._
import com.arangodb
import com.arangodb.entity.IndexEntity
import com.arangodb.model
import com.arangodb.model.{CollectionPropertiesOptions, GeoIndexOptions, PersistentIndexOptions, TtlIndexOptions}
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

  def ensure(waitForSync: Option[Boolean],
             schema: Option[CollectionSchema],
             computedValues: List[ComputedValue]): IO[Unit] = collection.info().flatMap { info =>
    if ((waitForSync.nonEmpty && !waitForSync.contains(info.waitForSync)) ||
        (schema.nonEmpty && !schema.contains(info.schema)) ||
        info.computedValues != computedValues) {
      io {
        val o = new CollectionPropertiesOptions
        val arangoSchema = new model.CollectionSchema
        schema.foreach { s =>
          s.rule.foreach(arangoSchema.setRule)
          s.level.foreach { l =>
            arangoSchema.setLevel(l match {
              case Level.Moderate => model.CollectionSchema.Level.MODERATE
              case Level.New => model.CollectionSchema.Level.NEW
              case Level.None => model.CollectionSchema.Level.NONE
              case Level.Strict => model.CollectionSchema.Level.STRICT
            })
          }
        }
        waitForSync.foreach(o.waitForSync(_))
        o.schema(arangoSchema)
        val arangoComputedValues = computedValues.map { cv =>
          val v = new model.ComputedValue()
          v.name(cv.name)
          v.expression(cv.expression)
          v.overwrite(cv.overwrite)
          v.computeOn(cv.computeOn.map {
            case ComputeOn.Insert => model.ComputedValue.ComputeOn.insert
            case ComputeOn.Update => model.ComputedValue.ComputeOn.update
            case ComputeOn.Replace => model.ComputedValue.ComputeOn.replace
          }.toList: _*)
          v.keepNull(cv.keepNull)
          v.failOnWarning(cv.failOnWarning)
          v
        }
        o.computedValues(arangoComputedValues: _*)
        _collection.changeProperties(o)
      }
    } else {
      IO.unit
    }
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