package com.outr.arango.pagination

import cats.effect.IO
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query.dsl.ref2Wrapped
import com.outr.arango.query.{Query, QueryPart, sc2AQL}
import com.outr.arango.util.Helpers.io
import com.outr.arango.{Document, DocumentModel, Field, Graph, Id, Index, Unique}
import fabric.Json
import fabric.rw.{Asable, RW}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait PaginationSupport extends Graph { graph =>
  protected def paginationMaintenanceSchedule: FiniteDuration = 10.minutes

  /**
    * Collection representing the pagination cache
    */
  val pagedResults: DocumentCollection[PagedResult, PagedResult.type] = vertex(PagedResult)

  /**
    * Maximum number of results able to be stored in the cache. If this overflows, subsequent queries will fallback to
    * ResultType.Realtime until there is availability to store more. This helps avoid risk of DoS style attacks or
    * overloading leading to storage issues with the database.
    */
  val pagedResultsMax: Int = 1_000_000

  /**
    * Maximum number of results to be cached per query.
    */
  val pagedResultsQueryMax: Int = 100_000

  override def init(createDatabase: Boolean = true, dropDatabase: Boolean = false): IO[Unit] = super
    .init(createDatabase, dropDatabase)
    .flatMap { _ =>
      maintenance().start.map(_ => ())
    }

  private def maintenance(): IO[Unit] = IO.sleep(paginationMaintenanceSchedule)
    .flatMap { _ =>
      pagination.doMaintenance()
    }
    .flatMap { _ =>
      maintenance()
    }

  object pagination {
    private val ReturnRegex = """\s*RETURN (.+)""".r

    def apply[R](query: Query,
                 pageSize: Int = 100,
                 resultType: ResultType = ResultType.CachedUpdated,
                 ttl: FiniteDuration = 5.minutes)
                (implicit rw: RW[R]): IO[Option[Page[R]]] = io(query.normalize).flatMap { query =>
      // TODO: Verify results isn't overflowing
      query.parts.last match {
        case QueryPart.Static(value) => value match {
          case ReturnRegex(returnRef) =>
            val returnPart = QueryPart.Static(returnRef)
            val queryId = Id[Query](Unique(), "query")
            val now = System.currentTimeMillis()
            val deleteAfter = now + ttl.toMillis
            val data = if (resultType == ResultType.Reference) QueryPart.Static("null") else returnPart
            val updatedQuery = Query(query.parts.init) +
              aql"""
                  INSERT {
                    queryId: $queryId,
                    resultType: $resultType,
                    recordId: $returnPart._id,
                    data: $data,
                    deleteAfter: $deleteAfter,
                    created: $now
                  } INTO $pagedResults
                 """
            execute(updatedQuery).flatMap { _ =>
              load[R](
                queryId = queryId,
                page = 0,
                pageSize = pageSize
              )
            }
          case _ => IO.raiseError(throw new RuntimeException(s"Last part did not contain RETURN: $value"))
        }
        case lastPart => IO.raiseError(new RuntimeException(s"Last part must be a Static part with RETURN but received: $lastPart"))
      }
    }

    def load[R](queryId: Id[Query],
                page: Int,
                pageSize: Int)
               (implicit rw: RW[R]): IO[Option[Page[R]]] = {
      val offset = page * pageSize
      for {
        countFiber <- pagedResults.query.byFilter(ref => ref.queryId === queryId).count.start
        resultsFiber <- pagedResults.query(
          aql"""
              FOR pr IN $pagedResults
              FILTER pr.${PagedResult.queryId} == $queryId
              LIMIT $offset, $pageSize
              // Updated data if CachedUpdated or Reference
              LET data1 = pr.${PagedResult.resultType} == 'Reference' ? {data: DOCUMENT(pr.${PagedResult.recordId})} : {}
              LET data2 = pr.${PagedResult.resultType} == 'CachedUpdated' ? {data: NOT_NULL(DOCUMENT(pr.${PagedResult.recordId}), pr.${PagedResult.data})} : data1
              RETURN MERGE(pr, data2)
             """).all.start
        count <- countFiber.joinWithNever
        results <- resultsFiber.joinWithNever
      } yield {
        if (results.isEmpty) {
          None
        } else {
          Some(Page[R](
            queryId = queryId,
            resultType = results.head.resultType,
            page = page,
            pageSize = pageSize,
            total = count,
            results = results,
            graph = graph
          ))
        }
      }
    }

    def doMaintenance(): IO[Unit] = {
      val now = System.currentTimeMillis()
      val query =
        aql"""
            FOR pr IN $pagedResults
            FILTER pr.${PagedResult.deleteAfter} <= $now
            REMOVE pr IN $pagedResults
           """
      execute(query)
    }
  }
}