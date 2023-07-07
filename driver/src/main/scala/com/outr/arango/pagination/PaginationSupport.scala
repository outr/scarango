package com.outr.arango.pagination

import com.outr.arango.query.Query
import com.outr.arango.{Document, DocumentModel, Graph, Id, Index}
import fabric.Json
import fabric.rw.RW

trait PaginationSupport {
  this: Graph =>
}

case class PagedResult(queryId: Id[Query],
                       resultType: ResultType,
                       recordId: Id[Any],
                       index: Long,
                       data: Option[Json],
                       created: Long = System.currentTimeMillis(),
                       _id: Id[PagedResult] = PagedResult.id()) extends Document[PagedResult]

object PagedResult extends DocumentModel[PagedResult] {
  override implicit val rw: RW[PagedResult] = RW.gen

  override val collectionName: String = "pagination"

  override def indexes: List[Index] = List(

  )
}

sealed trait ResultType

object ResultType {
  case object Reference extends ResultType
  case object Cached extends ResultType
}