package com.outr.arango

import cats.effect.IO
import com.arangodb.async.{ArangoCollectionAsync, ArangoDBAsync, ArangoDatabaseAsync}
import com.arangodb.model.CollectionCreateOptions
import com.outr.arango.util.JavaHelpers._

class ArangoDBServer(connection: ArangoDBAsync) {
  lazy val db: ArangoDB = new ArangoDB(connection.db())

  def db(name: String): ArangoDB = new ArangoDB(connection.db(name))
}

class ArangoDB(db: ArangoDatabaseAsync) {
  def create(): IO[Boolean] = db.create().toIO.map(_.booleanValue())

  def collection(name: String): ArangoDBCollection = new ArangoDBCollection(db.collection(name))
}

class ArangoDBCollection(collection: ArangoCollectionAsync) {
  def create(options: CreateCollectionOptions = CreateCollectionOptions()) = {
    val o = options
    collection.create(new CollectionCreateOptions {
      name(collection.name())
      o.journalSize.foreach(journalSize(_))
      o.replicationFactor.foreach(replicationFactor(_))
      o.satelite.foreach(satellite(_))
      o.minReplicationFactor.foreach(minReplicationFactor(_))
      o.keyOptions.foreach(k => keyOptions(k.allowUserKeys, k.`type`, k.increment.orNull, k.offset.orNull))
    }).toIO
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