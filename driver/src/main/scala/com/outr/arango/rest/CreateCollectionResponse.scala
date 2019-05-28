package com.outr.arango.rest

case class CreateCollectionResponse(id: String,
                                    name: String,
                                    waitForSync: Boolean,
                                    isSystem: Boolean,
                                    status: Int,
                                    `type`: Int,
                                    error: Boolean,
                                    code: Int)
