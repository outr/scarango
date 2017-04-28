package com.outr.arango.rest

case class QueryResponse[T](id: Option[String],
                            result: List[T],
                            hasMore: Boolean,
                            count: Option[Int],
                            cached: Boolean,
                            extra: QueryResponseExtras,
                            error: Boolean,
                            code: Int)
