package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPITransaction(collections: Value,
                              action: Option[String] = None,
                              lockTimeout: Option[Long] = None,
                              maxTransactionSize: Option[Long] = None,
                              params: Option[String] = None,
                              waitForSync: Option[Boolean] = None)

object PostAPITransaction {
  implicit val rw: ReaderWriter[PostAPITransaction] = ccRW
}