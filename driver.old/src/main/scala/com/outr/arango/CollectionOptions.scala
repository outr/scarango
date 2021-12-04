package com.outr.arango

import com.outr.arango.api.model.PostAPICollectionOpts

case class CollectionOptions(distributeShardsLike: Option[String] = None,
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
                             waitForSync: Option[Boolean] = None,
                             waitForSyncReplication: Option[Int] = None,
                             enforceReplicationFactor: Option[Int] = None)
