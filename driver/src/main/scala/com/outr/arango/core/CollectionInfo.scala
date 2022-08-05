package com.outr.arango.core

import com.outr.arango.CollectionType

case class CollectionInfo(name: String,
                          waitForSync: Boolean,
                          isVolatile: Boolean,
                          isSystem: Boolean,
                          status: CollectionStatus,
                          `type`: CollectionType,
                          schema: CollectionSchema)