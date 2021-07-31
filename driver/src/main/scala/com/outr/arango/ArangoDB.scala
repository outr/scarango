package com.outr.arango

import cats.effect.IO
import cats.implicits._
import com.arangodb.async.{ArangoCollectionAsync, ArangoDBAsync, ArangoDatabaseAsync}
import com.arangodb.entity.IndexEntity
import com.arangodb.model.{CollectionCreateOptions, DocumentCreateOptions, FulltextIndexOptions, GeoIndexOptions, PersistentIndexOptions, TtlIndexOptions}
import com.outr.arango.util.Helpers._
import fabric.Value

import scala.jdk.CollectionConverters._

class ArangoDBServer(connection: ArangoDBAsync) {
  lazy val db: ArangoDB = new ArangoDB(connection.db())

  def db(name: String): ArangoDB = new ArangoDB(connection.db(name))
}

object ArangoDBServer {
  def apply(connection: ArangoDBAsync): ArangoDBServer = new ArangoDBServer(connection)

  // TODO: add configuration options
  def apply(password: Option[String] = None): ArangoDBServer = apply(new ArangoDBAsync.Builder()
    .password(password.orNull)
    .build())
}

class ArangoDB(db: ArangoDatabaseAsync) {
  def create(): IO[Boolean] = db.create().toIO.map(_.booleanValue())

  def exists(): IO[Boolean] = db.exists().toIO.map(_.booleanValue())

  def drop(): IO[Boolean] = db.drop().toIO.map(_.booleanValue())

  object query {
    def parse(query: Query): IO[AQLParseResult] = {
      db.parseQuery(query.string).toIO.map(aqlParseEntityConversion)
    }

    def apply(query: Query): fs2.Stream[IO, Value] = {
      val bindVars: java.util.Map[String, AnyRef] = query.variables.map {
        case (key, value) => key -> value2AnyRef(value)
      }.asJava

      fs2.Stream.force(db.query(query.string, bindVars, classOf[String]).toIO.map { c =>
        // TODO: Consider c.stream() instead
        val cursor: java.util.Iterator[String] = c
        val iterator: Iterator[String] = cursor.asScala
        fs2.Stream.fromBlockingIterator[IO](iterator, 512)
      }).map(fabric.parse.Json.parse)
    }
  }

  def collection(name: String): ArangoDBCollection = new ArangoDBCollection(db.collection(name))
}

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

    // TODO: Update support
    // TODO: Delete support
//    def delete(key: String) = collection.deleteDocument(key, classOf[String], options).toIO.map(_ => ())

    object batch {
      def insert(docs: List[fabric.Obj], options: CreateOptions = CreateOptions.Insert): IO[CreateResults] = collection
        .insertDocuments(docs.map(fabric.parse.Json.format).asJava, options)
        .toIO
        .map(multiDocumentEntityConversion)

      def upsert(docs: List[fabric.Obj], options: CreateOptions = CreateOptions.Upsert): IO[CreateResults] = insert(docs, options)

      // TODO: delete support
    }
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
                                   satellite: Option[Boolean] = None,
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

case class AQLParseResult(collections: List[String], bindVars: List[String], ast: List[ASTNode])

case class ASTNode(`type`: String, subNodes: List[ASTNode], name: String, id: Long, value: AnyRef)

sealed trait OverwriteMode

object OverwriteMode {
  case object None extends OverwriteMode
  case object Ignore extends OverwriteMode
  case object Replace extends OverwriteMode
  case object Update extends OverwriteMode
  case object UpdateMerge extends OverwriteMode
  case object Conflict extends OverwriteMode
}

case class CreateOptions(waitForSync: Boolean = false,
                         returnNew: Boolean = false,
                         returnOld: Boolean = false,
                         overwrite: OverwriteMode = OverwriteMode.None,
                         silent: Boolean = true,
                         streamTransactionId: Option[String] = None)

object CreateOptions {
  val Insert: CreateOptions = CreateOptions()
  val Upsert: CreateOptions = CreateOptions(overwrite = OverwriteMode.Replace)
}

case class CreateResult(key: Option[String], id: Option[String], rev: Option[String], newDocument: Option[fabric.Value], oldDocument: Option[fabric.Value])

case class CreateResults(results: List[Either[ArangoError, CreateResult]]) {
  lazy val documents: List[CreateResult] = results.collect {
    case Right(cr) => cr
  }
  lazy val errors: List[ArangoError] = results.collect {
    case Left(e) => e
  }
}