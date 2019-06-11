package com.outr.arango.rest

case class TruncateCollectionResponse(id: String,
                                      name: String,
                                      isSystem: Boolean,
                                      status: Int,
                                      `type`: Int,
                                      error: Boolean,
                                      code: Int)
