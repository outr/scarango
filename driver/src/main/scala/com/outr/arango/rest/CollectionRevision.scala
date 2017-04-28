package com.outr.arango.rest

case class CollectionRevision(id: String,
                              name: String,
                              isSystem: Boolean,
                              status: Int,
                              `type`: Int,
                              revision: String,
                              error: Boolean,
                              code: Int)
