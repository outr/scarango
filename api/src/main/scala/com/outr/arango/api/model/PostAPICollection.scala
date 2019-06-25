package com.outr.arango.api.model

import io.circe.Json


case class PostAPICollection(name: String,
                             distributeShardsLike: Option[String] = None,
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
                             `type`: Option[Long] = None,
                             waitForSync: Option[Boolean] = None)