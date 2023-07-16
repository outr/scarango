package com.outr.arango.core

import com.arangodb.model
import com.arangodb.model.OptionsBuilder
import com.outr.arango.util.Helpers._

class ArangoDBCollectionCreateOptions(collectionName: String, o: CreateCollectionOptions) {
  private[arango] lazy val arango: model.CollectionCreateOptions = {
    val c = new model.CollectionCreateOptions

    o.replicationFactor.foreach(c.replicationFactor)
    o.writeConcern.foreach(c.writeConcern(_))
    o.keyOptions.foreach(k => c.keyOptions(k.allowUserKeys, k.`type`, k.increment, k.offset))
    o.waitForSync.foreach(c.waitForSync(_))
    o.computedValues.map { cv =>
      val v = new model.ComputedValue()
      v.name(cv.name)
      v.expression(cv.expression)
      v.overwrite(cv.overwrite)
      v.computeOn(cv.computeOn.map {
        case ComputeOn.Insert => model.ComputedValue.ComputeOn.insert
        case ComputeOn.Update => model.ComputedValue.ComputeOn.update
        case ComputeOn.Replace => model.ComputedValue.ComputeOn.replace
      }.toList: _*)
      v.keepNull(cv.keepNull)
      v.failOnWarning(cv.failOnWarning)
      v
    }
    c.shardKeys(o.shardKeys: _*)
    o.numberOfShards.foreach(c.numberOfShards(_))
    o.isSystem.foreach(c.isSystem(_))
    o.`type`.foreach(t => c.`type`(t))
    o.distributeShardsLike.foreach(c.distributeShardsLike)
    o.shardingStrategy.foreach(c.shardingStrategy)
    o.smartJoinAttribute.foreach(c.smartJoinAttribute)
    val schema = new model.CollectionSchema
    o.collectionSchema.rule.foreach(schema.setRule)
    o.collectionSchema.level.foreach { l =>
      schema.setLevel(l match {
        case Level.None => model.CollectionSchema.Level.NONE
        case Level.New => model.CollectionSchema.Level.NEW
        case Level.Moderate => model.CollectionSchema.Level.MODERATE
        case Level.Strict => model.CollectionSchema.Level.STRICT
      })
    }
    o.collectionSchema.message.foreach(schema.setMessage)
    c.schema(schema)

    OptionsBuilder.build(c, collectionName)
  }
}