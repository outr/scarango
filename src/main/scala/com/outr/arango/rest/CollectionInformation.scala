package com.outr.arango.rest

case class CollectionInformation(id: String,
                                 name: String,
                                 status: Int,
                                 `type`: Int,
                                 isSystem: Boolean)
