package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class QueryResponse[T](id: Option[String],
                            result: List[T],
                            hasMore: Boolean,
                            count: Int = -1,
                            cached: Boolean,
                            extra: QueryResponseExtras,
                            error: Boolean,
                            code: Int)

object QueryResponse {
  implicit def rw[T: ReaderWriter]: ReaderWriter[QueryResponse[T]] = ccRW
}