package com.outr.arango

case class QueryResponse[T](id: Option[String],
                            result: List[T],
                            hasMore: Boolean,
                            count: Int = -1,
                            cached: Boolean,
                            extra: QueryResponseExtras,
                            error: Boolean,
                            code: Int)