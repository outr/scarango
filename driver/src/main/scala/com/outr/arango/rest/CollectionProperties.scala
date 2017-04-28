package com.outr.arango.rest

case class CollectionProperties(waitForSync: Boolean,
                                doCompact: Boolean,
                                journalSize: Int,
                                keyOptions: KeyOptions,
                                isVolatile: Boolean,
                                numberOfShards: Option[Int],
                                shardKeys: Option[List[String]],
                                replicationFactor: Option[Int])
