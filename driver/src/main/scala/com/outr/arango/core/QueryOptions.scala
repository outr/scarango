package com.outr.arango.core

import com.arangodb.model.AqlQueryOptions

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
                        skipInaccessibleCollections: Option[Boolean] = None) {
  private[arango] lazy val arango: AqlQueryOptions = {
    val o = new AqlQueryOptions
    count.foreach(o.count(_))
    batchSize.foreach(o.batchSize(_))
    ttl.foreach(d => o.ttl(d.toSeconds.toInt))
    cache.foreach(o.cache(_))
    memoryLimit.foreach(o.memoryLimit(_))
    fullCount.foreach(o.fullCount(_))
    fillBlockCache.foreach(o.fillBlockCache(_))
    maxNumberOfPlans.foreach(o.maxPlans(_))
    maxWarningCount.foreach(o.maxWarningCount(_))
    failOnWarning.foreach(o.failOnWarning(_))
    allowRetry.foreach(o.allowRetry(_))
    stream.foreach(o.stream(_))
    profile.foreach(o.profile(_))
    satelliteSyncWait.foreach(d => o.satelliteSyncWait(d.toMillis / 1000.0))
    maxRuntime.foreach(d => o.maxRuntime(d.toMillis / 1000.0))
    maxTransactionSize.foreach(o.maxTransactionSize(_))
    intermediateCommitSize.foreach(o.intermediateCommitSize(_))
    intermediateCommitCount.foreach(o.intermediateCommitCount(_))
    skipInaccessibleCollections.foreach(o.skipInaccessibleCollections(_))
    o
  }
  // TODO: FINISH
}
