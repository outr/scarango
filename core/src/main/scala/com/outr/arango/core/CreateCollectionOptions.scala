package com.outr.arango.core

import com.outr.arango.CollectionType

case class CreateCollectionOptions(replicationFactor: Option[Int] = None,
                                   writeConcern: Option[Int] = None,
                                   keyOptions: Option[KeyOptions] = None,
                                   waitForSync: Option[Boolean] = None,
                                   computedValues: List[ComputedValue] = Nil,
                                   shardKeys: List[String] = Nil,
                                   numberOfShards: Option[Int] = None,
                                   isSystem: Option[Boolean] = None,
                                   `type`: Option[CollectionType] = None,
                                   distributeShardsLike: Option[String] = None,
                                   shardingStrategy: Option[String] = None,
                                   smartJoinAttribute: Option[String] = None,
                                   collectionSchema: CollectionSchema = CollectionSchema())