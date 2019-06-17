package com.outr.arango.api.model

import io.circe.Json

/**
  * PostAPIViewProps
  *
  * @param cleanupIntervalStep Wait at least this many commits between removing unused files in the
  *        ArangoSearch data directory (default: 10, to disable use: 0).
  *        For the case where the consolidation policies merge segments often (i.e. a lot
  *        of commit+consolidate), a lower value will cause a lot of disk space to be
  *        wasted.
  *        For the case where the consolidation policies rarely merge segments (i.e. few
  *        inserts/deletes), a higher value will impact performance without any added
  *        benefits.
  *        Background:
  *          With every "commit" or "consolidate" operation a new state of the view
  *          internal data-structures is created on disk.
  *          Old states/snapshots are released once there are no longer any users
  *          remaining.
  *          However, the files for the released states/snapshots are left on disk, and
  *          only removed by "cleanup" operation.
  * @param commitIntervalMsec Wait at least this many milliseconds between committing view data store
  *        changes and making documents visible to queries (default: 1000, to disable
  *        use: 0).
  *        For the case where there are a lot of inserts/updates, a lower value, until
  *        commit, will cause the index not to account for them and memory usage would
  *        continue to grow.
  *        For the case where there are a few inserts/updates, a higher value will impact
  *        performance and waste disk space for each commit call without any added
  *        benefits.
  *        Background:
  *          For data retrieval ArangoSearch views follow the concept of
  *          "eventually-consistent", i.e. eventually all the data in ArangoDB will be
  *          matched by corresponding query expressions.
  *          The concept of ArangoSearch view "commit" operation is introduced to
  *          control the upper-bound on the time until document addition/removals are
  *          actually reflected by corresponding query expressions.
  *          Once a "commit" operation is complete all documents added/removed prior to
  *          the start of the "commit" operation will be reflected by queries invoked in
  *          subsequent ArangoDB transactions, in-progress ArangoDB transactions will
  *          still continue to return a repeatable-read state.
  * @param consolidationIntervalMsec Wait at least this many milliseconds between applying 'consolidationPolicy' to
  *        consolidate view data store and possibly release space on the filesystem
  *        (default: 60000, to disable use: 0).
  *        For the case where there are a lot of data modification operations, a higher
  *        value could potentially have the data store consume more space and file handles.
  *        For the case where there are a few data modification operations, a lower value
  *        will impact performance due to no segment candidates available for
  *        consolidation.
  *        Background:
  *          For data modification ArangoSearch views follow the concept of a
  *          "versioned data store". Thus old versions of data may be removed once there
  *          are no longer any users of the old data. The frequency of the cleanup and
  *          compaction operations are governed by 'consolidationIntervalMsec' and the
  *          candidates for compaction are selected via 'consolidationPolicy'.
  * @param consolidationPolicy *** No description ***
  * @param links *** No description ***
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class PostAPIViewProps(cleanupIntervalStep: Option[Long] = None,
                            commitIntervalMsec: Option[Long] = None,
                            consolidationIntervalMsec: Option[Long] = None,
                            consolidationPolicy: Option[PostAPIViewPropsConsolidation] = None,
                            links: Option[Map[String, ArangoLinkProperties]])

case class ArangoLinkProperties(analyzers: List[String],
                                fields: Map[String, ArangoLinkFieldProperties],
                                includeAllFields: Boolean,
                                storeValues: String,
                                trackListPositions: Boolean)

case class ArangoLinkFieldProperties(analyzers: List[String])