package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class PostAPICursorOpts(failOnWarning: Option[Boolean] = None,
                             fullCount: Option[Boolean] = None,
                             intermediateCommitCount: Option[Long] = None,
                             intermediateCommitSize: Option[Long] = None,
                             maxPlans: Option[Long] = None,
                             maxTransactionSize: Option[Long] = None,
                             maxWarningCount: Option[Long] = None,
                             maxRuntime: Option[Double] = None,
                             optimizerRules: Option[List[String]] = None,
                             profile: Option[Int] = None,
                             satelliteSyncWait: Option[Boolean] = None,
                             skipInaccessibleCollections: Option[Boolean] = None,
                             stream: Option[Boolean] = None)

object PostAPICursorOpts {
  implicit val rw: ReaderWriter[PostAPICursorOpts] = ccRW
}