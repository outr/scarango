package com.outr.arango.rest

case class CollectionCount(id: String,
                           name: String,
                           isSystem: Boolean,
                           doCompact: Boolean,
                           isVolatile: Boolean,
                           journalSize: Long,
                           keyOptions: KeyOptions,
                           waitForSync: Boolean,
                           indexBuckets: Int,
                           count: Int,
                           status: Int,
                           `type`: Int,
                           error: Boolean,
                           code: Int)
