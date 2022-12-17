package com.outr.arango.core

import com.arangodb.model.CollectionCreateOptions
import com.outr.arango.util.Helpers._

class ArangoDBCollectionCreateOptions(collectionName: String, o: CreateCollectionOptions) extends CollectionCreateOptions {
  this.name(collectionName)
  o.`type`.foreach(t => `type`(t))
  o.replicationFactor.foreach(replicationFactor(_))
  o.satellite.foreach(satellite(_))
  o.writeConcern.foreach(writeConcern(_))
  o.keyOptions.foreach(k => keyOptions(k.allowUserKeys, k.`type`, k.increment, k.offset))
}