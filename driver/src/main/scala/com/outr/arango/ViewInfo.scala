package com.outr.arango

import com.outr.arango.api.model.ArangoLinkProperties

case class ViewInfo(globallyUniqueId: String,
                    id: String,
                    name: String,
                    `type`: String,
                    cleanupIntervalStep: Int,
                    consolidationIntervalMsec: Long,
                    consolidationPolicy: ViewConsolidationPolicy,
                    writebufferActive: Long,
                    writebufferIdle: Long,
                    writebufferSizeMax: Long,
                    links: Map[String, ArangoLinkProperties])
