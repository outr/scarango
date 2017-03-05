package com.outr.arango.rest

case class Collection(id: String,
                      name: String,
                      isSystem: Boolean,
                      status: Int,
                      `type`: Int)
