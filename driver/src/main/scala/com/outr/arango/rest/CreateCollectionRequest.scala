package com.outr.arango.rest

case class CreateCollectionRequest(name: String,
                                   journalSize: Option[Long],
                                   replicationFactor: Int,
                                   keyOptions: KeyOptions,
                                   waitForSync: Boolean,
                                   doCompact: Boolean,
                                   isVolatile: Boolean,
                                   shardKeys: Array[String],
                                   numberOfShards: Int,
                                   isSystem: Boolean,
                                   `type`: Int,
                                   indexBuckets: Int)
