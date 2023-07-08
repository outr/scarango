package com.outr.arango.pagination

import cats.effect.IO
import com.outr.arango.collection.{DocumentCollection, QueryBuilder}
import com.outr.arango.query.{Query, sc2AQL}
import com.outr.arango.{Document, DocumentModel, Field, Graph, Id, Index}
import fabric.Json
import fabric.rw.RW

import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait PaginationSupport {
  this: Graph =>

  protected def paginationMaintenanceSchedule: FiniteDuration = 10.minutes

  val pagedResults: DocumentCollection[PagedResult, PagedResult.type] = vertex(PagedResult)

  override def init(createDatabase: Boolean, dropDatabase: Boolean): IO[Unit] = this
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
    def apply[R](q: QueryBuilder[R],
                 ttl: FiniteDuration = 5.minutes): IO[Page[R]] = for {
      // TODO: Execute query
    } yield ???

    def load[R](queryId: Id[Query],
                page: Int,
                pageSize: Int)
               (implicit rw: RW[R])

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

case class Page[R](queryId: Id[Query],
                   page: Int,
                   pageSize: Int,
                   total: Int,
                   results: List[R],
                   graph: Graph with PaginationSupport) {
  lazy val offset: Int = page * pageSize
  lazy val pages: Int = math.ceil(total.toDouble / pageSize.toDouble).toInt
  lazy val hasPrevious: Boolean = page > 0
  lazy val hasNext: Boolean = page < pages - 1


}

case class PagedResult(queryId: Id[Query],
                       resultType: ResultType,
                       recordId: Id[Any],
                       position: Long,
                       data: Option[Json],
                       deleteAfter: Long,
                       created: Long = System.currentTimeMillis(),
                       _id: Id[PagedResult] = PagedResult.id()) extends Document[PagedResult]

object PagedResult extends DocumentModel[PagedResult] {
  override implicit val rw: RW[PagedResult] = RW.gen

  lazy val queryId: Field[Id[Query]] = field("queryId")
  lazy val resultType: Field[ResultType] = field("resultType")
  lazy val recordId: Field[Id[Any]] = field("recordId")
  lazy val position: Field[Long] = field("position")
  lazy val data: Field[Option[Json]] = field("data")
  lazy val deleteAfter: Field[Long] = field("deleteAfter")
  lazy val created: Field[Long] = field("created")

  override val collectionName: String = "pagination"

  override def indexes: List[Index] = List(
    queryId.index.persistent(),
    recordId.index.persistent(),
    position.index.persistent(),
    deleteAfter.index.persistent(),
    created.index.persistent()
  )
}

sealed trait ResultType

object ResultType {
  implicit lazy val rw: RW[ResultType] = RW.enumeration(List(Reference, Cached))

  case object Reference extends ResultType
  case object Cached extends ResultType
}