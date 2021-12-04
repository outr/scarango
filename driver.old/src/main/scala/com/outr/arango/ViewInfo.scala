package com.outr.arango

import com.outr.arango.api.model.{ArangoLinkProperties, PostAPIViewProps}
import fabric.rw.{ReaderWriter, ccRW}

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

object ViewInfo {
  implicit def linksRW: ReaderWriter[Map[String, ArangoLinkProperties]] = PostAPIViewProps.linksRW
  implicit val rw: ReaderWriter[ViewInfo] = ccRW
}