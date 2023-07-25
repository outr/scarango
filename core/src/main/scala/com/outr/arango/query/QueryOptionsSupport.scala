package com.outr.arango.query

import scala.concurrent.duration.FiniteDuration

trait QueryOptionsSupport[T] {
  def withOptions(f: QueryOptions => QueryOptions): T

  def withCount(count: Boolean = true): T = withOptions(_.copy(
    count = Some(count)
  ))

  def withBatchSize(batchSize: Int): T = withOptions(_.copy(
    batchSize = Some(batchSize)
  ))

  def withTTL(ttl: FiniteDuration): T = withOptions(_.copy(
    ttl = Some(ttl)
  ))

  def withCache(cache: Boolean = true): T = withOptions(_.copy(
    cache = Some(cache)
  ))

  def withMemoryLimit(limit: Long): T = withOptions(_.copy(
    memoryLimit = Some(limit)
  ))

  def withFullCount(fullCount: Boolean = true): T = withOptions(_.copy(
    fullCount = Some(fullCount)
  ))

  def withFillBlockCache(fill: Boolean = true): T = withOptions(_.copy(
    fillBlockCache = Some(fill)
  ))

  def withMaxNumberOfPlans(plans: Int): T = withOptions(_.copy(
    maxNumberOfPlans = Some(plans)
  ))

  def withMaxWarningCount(max: Int): T = withOptions(_.copy(
    maxWarningCount = Some(max)
  ))

  def withFailOnWarning(fail: Boolean = true): T = withOptions(_.copy(
    failOnWarning = Some(fail)
  ))

  def withAllowRetry(retry: Boolean = true): T = withOptions(_.copy(
    allowRetry = Some(retry)
  ))

  def withStream(stream: Boolean = true): T = withOptions(_.copy(
    stream = Some(stream)
  ))

  def withProfile(profile: Boolean = true): T = withOptions(_.copy(
    profile = Some(profile)
  ))

  def withSatelliteSyncWait(wait: FiniteDuration): T = withOptions(_.copy(
    satelliteSyncWait = Some(wait)
  ))

  def withMaxRuntime(max: FiniteDuration): T = withOptions(_.copy(
    maxRuntime = Some(max)
  ))

  def withMaxTransactionSize(max: Long): T = withOptions(_.copy(
    maxTransactionSize = Some(max)
  ))

  def withIntermediateCommitSize(size: Long): T = withOptions(_.copy(
    intermediateCommitSize = Some(size)
  ))

  def withIntermediateCommitCount(count: Long): T = withOptions(_.copy(
    intermediateCommitCount = Some(count)
  ))

  def withSkipInaccessibleCollections(skip: Boolean = true): T = withOptions(_.copy(
    skipInaccessibleCollections = Some(skip)
  ))
}
