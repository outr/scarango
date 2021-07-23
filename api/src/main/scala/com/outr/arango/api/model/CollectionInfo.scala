package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionInfo(keyOptions: KeyGeneratorType,
                          doCompact: Option[Boolean] = None,
                          globallyUniqueId: Option[String] = None,
                          id: Option[String] = None,
                          indexBuckets: Option[Int] = None,
                          isSystem: Option[Boolean] = None,
                          isVolatile: Option[Boolean] = None,
                          journalSize: Option[Int] = None,
                          name: Option[String] = None,
                          numberOfShards: Option[Int] = None,
                          replicationFactor: Option[Int] = None,
                          shardKeys: Option[List[String]] = None,
                          shardingStrategy: Option[String] = None,
                          smartGraphAttribute: Option[String] = None,
                          status: Option[Int] = None,
                          statusString: Option[String] = None,
                          `type`: Option[Int] = None,
                          waitForSync: Option[Boolean] = None)

object CollectionInfo {
  implicit val rw: ReaderWriter[CollectionInfo] = ccRW
}