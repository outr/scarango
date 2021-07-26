package com.outr.arango

import cats.effect.IO
import cats.implicits._
import com.arangodb.async.{ArangoCollectionAsync, ArangoDBAsync, ArangoDatabaseAsync}
import com.arangodb.entity.{BaseDocument, IndexEntity}
import com.arangodb.model.{CollectionCreateOptions, FulltextIndexOptions, GeoIndexOptions, PersistentIndexOptions, TtlIndexOptions}
import com.outr.arango.util.Helpers._

import scala.jdk.CollectionConverters._

class ArangoDBServer(connection: ArangoDBAsync) {
  lazy val db: ArangoDB = new ArangoDB(connection.db())

  def db(name: String): ArangoDB = new ArangoDB(connection.db(name))
}

class ArangoDB(db: ArangoDatabaseAsync) {
  // TODO: db.parseQuery to validate queries in AQL interpolation

  def create(): IO[Boolean] = db.create().toIO.map(_.booleanValue())

  object query {
    def apply(query: Query) = {
      val bindVars: java.util.Map[String, AnyRef] = query.variables.map {
        case (key, value) => key -> value2AnyRef(value)
      }.asJava

      fs2.Stream.force(db.query(query.string, bindVars, classOf[BaseDocument]).toIO.map { c =>
        c.stream().count()
        val cursor: java.util.Iterator[BaseDocument] = c
        val iterator: Iterator[BaseDocument] = cursor.asScala
        fs2.Stream.fromBlockingIterator[IO](iterator, 512)
      })
    }
  }

  def collection(name: String): ArangoDBCollection = new ArangoDBCollection(db.collection(name))
}

class ArangoDBCollection(collection: ArangoCollectionAsync) {
  // TODO: insert documents
  // TODO: queries

  def create(options: CreateCollectionOptions = CreateCollectionOptions()): IO[CollectionInfo] = {
    val o = options
    collection.create(new CollectionCreateOptions {
      name(collection.name())
      o.journalSize.foreach(journalSize(_))
      o.replicationFactor.foreach(replicationFactor(_))
      o.satelite.foreach(satellite(_))
      o.minReplicationFactor.foreach(minReplicationFactor(_))
      o.keyOptions.foreach(k => keyOptions(k.allowUserKeys, k.`type`, k.increment, k.offset))
    }).toIO.map(collectionEntityConversion)
  }

  def info(): IO[CollectionInfo] = collection.getInfo.toIO.map(collectionEntityConversion)

  object document {
//    def insert(doc: fabric.Obj) = collection.insertDocument()
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

case class CreateCollectionOptions(journalSize: Option[Long] = None,
                                   replicationFactor: Option[Int] = None,
                                   satelite: Option[Boolean] = None,
                                   minReplicationFactor: Option[Int] = None,
                                   keyOptions: Option[KeyOptions] = None,
                                   waitForSync: Option[Boolean] = None,
                                   doCompact: Option[Boolean] = None,
                                   isVolatile: Option[Boolean] = None,
                                   shardKeys: Option[List[String]] = None,
                                   numberOfShards: Option[Int] = None,
                                   isSystem: Option[Boolean] = None,
                                   `type`: Option[CollectionType] = None,
                                   indexBuckets: Option[Int] = None,
                                   distributeShardsLike: Option[String] = None,
                                   shardingStrategy: Option[String] = None,
                                   smartJoinAttribute: Option[String] = None,
                                   collectionSchema: CollectionSchema = CollectionSchema())

case class KeyOptions(allowUserKeys: Boolean, `type`: KeyType, increment: Option[Int], offset: Option[Int])

sealed trait KeyType

object KeyType {
  case object Traditional extends KeyType
  case object AutoIncrement extends KeyType
  case object UUID extends KeyType
  case object Padded extends KeyType
}

case class CollectionSchema(rule: Option[String] = None, level: Option[Level] = None, message: Option[String] = None)

sealed trait Level

object Level {
  case object None extends Level
  case object New extends Level
  case object Moderate extends Level
  case object Strict extends Level
}

case class CollectionInfo(id: String,
                          name: String,
                          waitForSync: Boolean,
                          isVolatile: Boolean,
                          isSystem: Boolean,
                          status: CollectionStatus,
                          `type`: CollectionType,
                          schema: CollectionSchema)

sealed trait CollectionStatus

object CollectionStatus {
  case object New extends CollectionStatus
  case object Unloaded extends CollectionStatus
  case object Loaded extends CollectionStatus
  case object Loading extends CollectionStatus
  case object Deleted extends CollectionStatus
}