package com.outr.arango.rest

case class CollectionCount(id: String,
                           name: String,
                           isSystem: Boolean,
                           doCompact: Option[Boolean],
                           isVolatile: Option[Boolean],
                           journalSize: Option[Long],
                           keyOptions: KeyOptions,
                           waitForSync: Boolean,
                           indexBuckets: Option[Int],
                           count: Int,
                           status: Int,
                           `type`: Int,
                           error: Boolean,
                           code: Int)
