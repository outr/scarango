package com.outr.arango

import com.outr.arango.api.{APICollection, APICollectionCollectionName, APIDatabase, APIDatabaseDatabaseName}
import com.outr.arango.api.model.{CollectionInfo, GetAPIDatabaseNew, PostAPICollection, PostAPICollectionOpts}
import com.outr.arango.model.ArangoResponse
import io.youi.client.HttpClient
import io.youi.net.Path
import profig.JsonUtil

import scala.concurrent.Future
import scribe.Execution.global

class ArangoDatabase(client: HttpClient, name: String) {
  def create(): Future[ArangoResponse[Boolean]] = {
    APIDatabase.post(client, GetAPIDatabaseNew(
      name = name
    )).map(JsonUtil.fromJson[ArangoResponse[Boolean]](_))
    // TODO: Support setting user
  }

  def drop(): Future[ArangoResponse[Boolean]] = {
    APIDatabaseDatabaseName.delete(client, name).map(JsonUtil.fromJson[ArangoResponse[Boolean]](_))
  }

  def collection(name: String): ArangoCollection = {
    new ArangoCollection(client.path(Path.parse(s"/_db/${this.name}/")), this.name, name)
  }
}

class ArangoCollection(client: HttpClient, dbName: String, collectionName: String) {
  def create(distributeShardsLike: Option[String] = None,
             doCompact: Option[Boolean] = None,
             indexBuckets: Option[Long] = None,
             isSystem: Option[Boolean] = None,
             isVolatile: Option[Boolean] = None,
             journalSize: Option[Long] = None,
             keyOptions: Option[PostAPICollectionOpts] = None,
             numberOfShards: Long = 1L,
             replicationFactor: Long = 1L,
             shardKeys: List[String] = List("_key"),
             shardingStrategy: Option[String] = None,
             smartJoinAttribute: Option[String] = None,
             `type`: CollectionType = CollectionType.Document,
             waitForSync: Option[Boolean] = None,
             waitForSyncReplication: Option[Int] = None,
             enforceReplicationFactor: Option[Int] = None): Future[CollectionInfo] = {
    val collectionType = `type` match {
      case CollectionType.Document => 2L
      case CollectionType.Edge => 3L
    }
    APICollection.post(
      client = client,
      body = PostAPICollection(
        name = collectionName,
        distributeShardsLike = distributeShardsLike,
        doCompact = doCompact,
        indexBuckets = indexBuckets,
        isSystem = isSystem,
        isVolatile = isVolatile,
        journalSize = journalSize,
        keyOptions = keyOptions,
        numberOfShards = numberOfShards,
        replicationFactor = replicationFactor,
        shardKeys = shardKeys,
        shardingStrategy = shardingStrategy,
        smartJoinAttribute = smartJoinAttribute,
        `type` = Some(collectionType),
        waitForSync = waitForSync
      ),
      waitForSyncReplication = waitForSyncReplication,
      enforceReplicationFactor = enforceReplicationFactor
    )
  }

  def drop(isSystem: Boolean = false): Future[Boolean] = APICollectionCollectionName
    .delete(client, collectionName, isSystem = Some(isSystem))
    .map(JsonUtil.fromJson[ArangoResponse[Option[Boolean]]](_))
    .map(!_.error)
}

sealed trait CollectionType

object CollectionType {
  case object Document extends CollectionType
  case object Edge extends CollectionType
}