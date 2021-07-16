package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPIReplicationApplierAdjust(endpoint: String,
                                          adaptivePolling: Option[Boolean] = None,
                                          autoResync: Option[Boolean] = None,
                                          autoResyncRetries: Option[Long] = None,
                                          autoStart: Option[Boolean] = None,
                                          chunkSize: Option[Long] = None,
                                          connectTimeout: Option[Long] = None,
                                          connectionRetryWaitTime: Option[Long] = None,
                                          database: Option[String] = None,
                                          idleMaxWaitTime: Option[Long] = None,
                                          idleMinWaitTime: Option[Long] = None,
                                          includeSystem: Option[Boolean] = None,
                                          initialSyncMaxWaitTime: Option[Long] = None,
                                          maxConnectRetries: Option[Long] = None,
                                          password: Option[String] = None,
                                          requestTimeout: Option[Long] = None,
                                          requireFromPresent: Option[Boolean] = None,
                                          restrictCollections: Option[List[String]] = None,
                                          restrictType: Option[String] = None,
                                          username: Option[String] = None,
                                          verbose: Option[Boolean] = None)

object PutAPIReplicationApplierAdjust {
  implicit val rw: ReaderWriter[PutAPIReplicationApplierAdjust] = ccRW
}