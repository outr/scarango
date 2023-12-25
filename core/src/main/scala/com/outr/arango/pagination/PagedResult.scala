package com.outr.arango.pagination

import com.outr.arango.query.Query
import com.outr.arango.{Document, DocumentModel, Field, Id, Index}
import fabric.Json
import fabric.rw._

case class PagedResult(queryId: Id[Query],
                       resultType: ResultType,
                       recordId: Id[Any],
                       data: Option[Json],
                       deleteAfter: Long,
                       created: Long = System.currentTimeMillis(),
                       _id: Id[PagedResult] = PagedResult.id()) extends Document[PagedResult]

object PagedResult extends DocumentModel[PagedResult] {
  override implicit val rw: RW[PagedResult] = RW.gen

  lazy val queryId: Field[Id[Query]] = field("queryId")
  lazy val resultType: Field[ResultType] = field("resultType")
  lazy val recordId: Field[Id[Document[_]]] = field("recordId")
  lazy val data: Field[Option[Json]] = field("data")
  lazy val deleteAfter: Field[Long] = field("deleteAfter")
  lazy val created: Field[Long] = field("created")

  override val collectionName: String = "pagination"

  override def indexes: List[Index] = List(
    queryId.index.persistent(),
    recordId.index.persistent(),
    deleteAfter.index.persistent(),
    created.index.persistent()
  )
}