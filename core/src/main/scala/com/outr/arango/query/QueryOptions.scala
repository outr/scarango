package com.outr.arango.query

import scala.concurrent.duration.FiniteDuration

case class QueryOptions(count: Option[Boolean] = None,
                        batchSize: Option[Int] = None,
                        ttl: Option[FiniteDuration] = None,
                        cache: Option[Boolean] = None,
                        memoryLimit: Option[Long] = None,
                        fullCount: Option[Boolean] = None,
                        fillBlockCache: Option[Boolean] = None,
                        maxNumberOfPlans: Option[Int] = None,
                        maxWarningCount: Option[Long] = None,
                        failOnWarning: Option[Boolean] = None,
                        allowRetry: Option[Boolean] = None,
                        stream: Option[Boolean] = None,
                        profile: Option[Boolean] = None,
                        satelliteSyncWait: Option[FiniteDuration] = None,
                        maxRuntime: Option[FiniteDuration] = None,
                        maxTransactionSize: Option[Long] = None,
                        intermediateCommitSize: Option[Long] = None,
                        intermediateCommitCount: Option[Long] = None,
                        skipInaccessibleCollections: Option[Boolean] = None)

object QueryOptions {
  def merge(options: List[QueryOptions]): QueryOptions = options.foldLeft(QueryOptions())((merged, o) => {
    merged.copy(
      count = o.count.orElse(merged.count),
      batchSize = o.batchSize.orElse(merged.batchSize),
      ttl = o.ttl.orElse(merged.ttl),
      cache = o.cache.orElse(merged.cache),
      memoryLimit = o.memoryLimit.orElse(merged.memoryLimit),
      fullCount = o.fullCount.orElse(merged.fullCount),
      fillBlockCache = o.fillBlockCache.orElse(merged.fillBlockCache),
      maxNumberOfPlans = o.maxNumberOfPlans.orElse(merged.maxNumberOfPlans),
      maxWarningCount = o.maxWarningCount.orElse(merged.maxWarningCount),
      failOnWarning = o.failOnWarning.orElse(merged.failOnWarning),
      allowRetry = o.allowRetry.orElse(merged.allowRetry),
      stream = o.stream.orElse(merged.stream),
      profile = o.profile.orElse(merged.profile),
      satelliteSyncWait = o.satelliteSyncWait.orElse(merged.satelliteSyncWait),
      maxRuntime = o.maxRuntime.orElse(merged.maxRuntime),
      maxTransactionSize = o.maxTransactionSize.orElse(merged.maxTransactionSize),
      intermediateCommitSize = o.intermediateCommitSize.orElse(merged.intermediateCommitSize),
      intermediateCommitCount = o.intermediateCommitCount.orElse(merged.intermediateCommitCount),
      skipInaccessibleCollections = o.skipInaccessibleCollections.orElse(merged.skipInaccessibleCollections)
    )
  })
}