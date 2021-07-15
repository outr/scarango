package com.outr.arango

import com.outr.arango.api.model.{CollectionInfo, PostAPICollection, PostAPICollectionOpts}
import com.outr.arango.api.{APICollection, APICollectionCollectionName, APICollectionCollectionNameLoad, APICollectionCollectionNameTruncate, APICollectionCollectionNameUnload, APIWalTail, WALOperations}
import com.outr.arango.model.ArangoResponse
import io.youi.client.HttpClient

import scala.concurrent.{ExecutionContext, Future}

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
             enforceReplicationFactor: Option[Int] = None)
            (implicit ec: ExecutionContext): Future[CollectionInfo] = {
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

  lazy val index: ArangoIndex = new ArangoIndex(client, dbName, collectionName)
  lazy val document: ArangoDocument = new ArangoDocument(client, dbName, collectionName)

  def unload()(implicit ec: ExecutionContext): Future[CollectionLoad] = {
    APICollectionCollectionNameUnload.put(client, collectionName).map(JsonUtil.fromJson[CollectionLoad](_))
  }

  def load()(implicit ec: ExecutionContext): Future[CollectionLoad] = {
    APICollectionCollectionNameLoad.put(client, collectionName).map(JsonUtil.fromJson[CollectionLoad](_))
  }

  def truncate()(implicit ec: ExecutionContext): Future[TruncateCollectionResponse] = {
    APICollectionCollectionNameTruncate.put(client, collectionName).map(JsonUtil.fromJson[TruncateCollectionResponse](_))
  }

  def drop(isSystem: Boolean = false)(implicit ec: ExecutionContext): Future[Boolean] = APICollectionCollectionName
    .delete(client, collectionName, isSystem = Some(isSystem))
    .map(JsonUtil.fromJson[ArangoResponse[Option[Boolean]]](_))
    .map(!_.error)
}