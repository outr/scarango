package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPIReplicationSynchronize(endpoint: String,
                                        database: Option[String] = None,
                                        includeSystem: Option[Boolean] = None,
                                        incremental: Option[Boolean] = None,
                                        initialSyncMaxWaitTime: Option[Long] = None,
                                        password: Option[String] = None,
                                        restrictCollections: Option[List[String]] = None,
                                        restrictType: Option[String] = None,
                                        username: Option[String] = None)

object PutAPIReplicationSynchronize {
  implicit val rw: ReaderWriter[PutAPIReplicationSynchronize] = ccRW
}