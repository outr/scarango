package com.outr.arango.rest

case class CollectionProperties(id: Option[String],
                                name: Option[String],
                                isSystem: Option[Boolean],
                                status: Option[Int],
                                `type`: Option[Int],
                                waitForSync: Boolean,
                                doCompact: Option[Boolean],
                                journalSize: Option[Int],
                                keyOptions: KeyOptions,
                                isVolatile: Option[Boolean],
                                numberOfShards: Option[Int],
                                shardKeys: Option[List[String]],
                                replicationFactor: Option[Int])
