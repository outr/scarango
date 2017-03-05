package com.outr.arango.rest

case class QueryRequest(query: String, count: Boolean, batchSize: Int)
