//package com.outr.arango.core
//
//import com.arangodb.model.AqlQueryOptions
//
//import scala.concurrent.duration.FiniteDuration
//
//case class QueryOptions(count: Option[Boolean] = None,
//                        batchSize: Option[Int] = None,
//                        ttl: Option[FiniteDuration] = None,
//                        cache: Option[Boolean] = None,
//                        memoryLimit: Option[Long] = None,
//                        fullCount: Option[Boolean] = None,
//                        fillBlockCache: Option[Boolean] = None,
//                        maxNumberOfPlans: Option[Int] = None,
//                        maxNodesPerCallstack: Option[Int] = None,
//                        maxWarningCount: Option[Int] = None,
//                        failOnWarning: Option[Boolean] = None,
//                        allowRetry: Option[Boolean] = None,
//                        stream: Option[Boolean] = None,
//                        profile: Option[Boolean] = None,
//                        satelliteSyncWait: Option[Boolean] = None,
//                        maxRuntime: Option[FiniteDuration] = None,
//                        maxTransactionSize: Option[Long] = None,
//                        intermediateCommitSize: Option[Long] = None,
//                        intermediateCommitCount: Option[Long] = None,
//                        skipInaccessibleCollections: Option[Boolean] = None) {
//  private[arango] lazy val arango: AqlQueryOptions = {
//    val o = new AqlQueryOptions
//    count.foreach(o.count)
//    o
//  }
//  // TODO: FINISH
//}
