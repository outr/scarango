package com.outr.arango.rest

import io.circe.Json

case class QueryResponse(result: List[Json],
                         hasMore: Boolean,
                         count: Int,
                         cached: Boolean,
                         extra: QueryResponseExtras,
                         error: Boolean,
                         code: Int)
