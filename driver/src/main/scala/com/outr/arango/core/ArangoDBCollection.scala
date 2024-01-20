package com.outr.arango.core

import cats.effect.IO
import cats.implicits._
import com.arangodb
import com.arangodb.entity.{IndexEntity, InvertedIndexField}
import com.arangodb.model
import com.arangodb.model.{CollectionPropertiesOptions, GeoIndexOptions, InvertedIndexOptions, PersistentIndexOptions, TtlIndexOptions}
import com.outr.arango.mutation.DataMutation
import com.outr.arango.util.Helpers._
import com.outr.arango.{AnalyzerFeature, Field, Index, IndexInfo}
import com.arangodb.entity.arangosearch.{AnalyzerFeature => AF}
import fabric.Json
import fabric.io.JsonFormatter
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
          s.rule.foreach(json => arangoSchema.setRule(JsonFormatter.Default(json)))
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
      val generate: List[IO[IndexInfo]] = indexes.map {
        case Index.Persistent(fields, sparse, unique, estimates) =>
          val options = new PersistentIndexOptions
          options.sparse(sparse)
          options.unique(unique)
          options.estimates(estimates)
          io(_collection.ensurePersistentIndex(fields.asJava, options))
        case Index.Geo(fields, geoJson) =>
          val options = new GeoIndexOptions
          options.geoJson(geoJson)
          io(_collection.ensureGeoIndex(fields.asJava, options))
        case Index.TTL(fields, expireAfterSeconds) =>
          val options = new TtlIndexOptions
          options.expireAfter(expireAfterSeconds)
          io(_collection.ensureTtlIndex(fields.asJava, options))
        case Index.Inverted(parallelism, fields, analyzer, features, includeAllFields, trackListPositions, searchField, cache, primaryKeyCache) =>
          def convert(features: Set[AnalyzerFeature]): Seq[AF] = features.map {
            case AnalyzerFeature.Frequency => AF.frequency
            case AnalyzerFeature.Norm => AF.norm
            case AnalyzerFeature.Position => AF.position
            case AnalyzerFeature.Offset => AF.offset
          }.toSeq
          val options = new InvertedIndexOptions
          options.parallelism(parallelism)
          options.fields(fields.map { f =>
            val i = new InvertedIndexField
            i.name(f.name)
            i.analyzer(f.analyzer.name)
            i.includeAllFields(f.includeAllFields)
            i.searchField(f.searchField)
            i.trackListPositions(f.trackListPositions)
            i.cache(f.cache)
            i.features(convert(f.features): _*)
            i
          }: _*)
          options.analyzer(analyzer.name)
          options.features(convert(features): _*)
          options.includeAllFields(includeAllFields)
          options.trackListPositions(trackListPositions)
          options.searchField(searchField)
          options.cache(cache)
          options.primaryKeyCache(primaryKeyCache)

          io(_collection.ensureInvertedIndex(options))
        case Index.Primary(_) => throw new UnsupportedOperationException(s"Primary indexes are created automatically!")
      }
      generate.sequence
    }

    def delete(indexIds: List[String]): IO[List[String]] = {
      indexIds.map { indexId =>
        io(_collection.deleteIndex(indexId))
      }.sequence
    }
  }
}