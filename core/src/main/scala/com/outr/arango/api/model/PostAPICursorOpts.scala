package com.outr.arango.api.model

/**
  * PostAPICursorOpts
  *
  * @param failOnWarning When set to *true*, the query will throw an exception and abort instead of producing
  *        a warning. This option should be used during development to catch potential issues
  *        early. When the attribute is set to *false*, warnings will not be propagated to
  *        exceptions and will be returned with the query result.
  *        There is also a server configuration option `--query.fail-on-warning` for setting the
  *        default value for *failOnWarning* so it does not need to be set on a per-query level.
  * @param fullCount if set to *true* and the query contains a *LIMIT* clause, then the
  *        result will have an *extra* attribute with the sub-attributes *stats*
  *        and *fullCount*, `{ ... , "extra": { "stats": { "fullCount": 123 } } }`.
  *        The *fullCount* attribute will contain the number of documents in the result before the
  *        last top-level LIMIT in the query was applied. It can be used to count the number of 
  *        documents that match certain filter criteria, but only return a subset of them, in one go.
  *        It is thus similar to MySQL's *SQL_CALC_FOUND_ROWS* hint. Note that setting the option
  *        will disable a few LIMIT optimizations and may lead to more documents being processed,
  *        and thus make queries run longer. Note that the *fullCount* attribute may only
  *        be present in the result if the query has a top-level LIMIT clause and the LIMIT 
  *        clause is actually used in the query.
  * @param intermediateCommitCount Maximum number of operations after which an intermediate commit is performed
  *        automatically. Honored by the RocksDB storage engine only.
  * @param intermediateCommitSize Maximum total size of operations after which an intermediate commit is performed
  *        automatically. Honored by the RocksDB storage engine only.
  * @param maxPlans Limits the maximum number of plans that are created by the AQL query optimizer.
  * @param maxTransactionSize Transaction size limit in bytes. Honored by the RocksDB storage engine only.
  * @param maxWarningCount Limits the maximum number of warnings a query will return. The number of warnings
  *        a query will return is limited to 10 by default, but that number can be increased
  *        or decreased by setting this attribute.
  * @param optimizer.rules A list of to-be-included or to-be-excluded optimizer rules
  *        can be put into this attribute, telling the optimizer to include or exclude
  *        specific rules. To disable a rule, prefix its name with a `-`, to enable a rule, prefix it
  *        with a `+`. There is also a pseudo-rule `all`, which will match all optimizer rules.
  * @param profile If set to *true* or *1*, then the additional query profiling information will be returned
  *        in the sub-attribute *profile* of the *extra* return attribute, if the query result
  *        is not served from the query cache. Set to *2* the query will include execution stats
  *        per query plan node in sub-attribute *stats.nodes* of the *extra* return attribute.
  *        Additionally the query plan is returned in the sub-attribute *extra.plan*.
  * @param satelliteSyncWait This *Enterprise Edition* parameter allows to configure how long a DBServer will have time
  *        to bring the satellite collections involved in the query into sync.
  *        The default value is *60.0* (seconds). When the max time has been reached the query
  *        will be stopped.
  * @param skipInaccessibleCollections AQL queries (especially graph traversals) will treat collection to which a user has no access rights as if these collections were empty. Instead of returning a forbidden access error, your queries will execute normally. This is intended to help with certain use-cases: A graph contains several collections and different users execute AQL queries on that graph. You can now naturally limit the accessible results by changing the access rights of users on collections. This feature is only available in the Enterprise Edition.
  * @param stream Specify *true* and the query will be executed in a **streaming** fashion. The query result is
  *        not stored on the server, but calculated on the fly. *Beware*: long-running queries will
  *        need to hold the collection locks for as long as the query cursor exists. 
  *        When set to *false* a query will be executed right away in its entirety. 
  *        In that case query results are either returned right away (if the result set is small enough),
  *        or stored on the arangod instance and accessible via the cursor API (with respect to the `ttl`). 
  *        It is advisable to *only* use this option on short-running queries or without exclusive locks 
  *        (write-locks on MMFiles).
  *        Please note that the query options `cache`, `count` and `fullCount` will not work on streaming queries.
  *        Additionally query statistics, warnings and profiling data will only be available after the query is finished.
  *        The default value is *false*
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class PostAPICursorOpts(failOnWarning: Option[Boolean] = None,
                             fullCount: Option[Boolean] = None,
                             intermediateCommitCount: Option[Long] = None,
                             intermediateCommitSize: Option[Long] = None,
                             maxPlans: Option[Long] = None,
                             maxTransactionSize: Option[Long] = None,
                             maxWarningCount: Option[Long] = None,
                             optimizerRules: Option[List[String]] = None,
                             profile: Option[Int] = None,
                             satelliteSyncWait: Option[Boolean] = None,
                             skipInaccessibleCollections: Option[Boolean] = None,
                             stream: Option[Boolean] = None)