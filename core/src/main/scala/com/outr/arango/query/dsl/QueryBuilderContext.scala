package com.outr.arango.query.dsl

import com.outr.arango.query.Query

class QueryBuilderContext private() {
  private var queries = List.empty[Query]

  def addQuery(query: Query): Unit = queries = query :: queries

  def toQuery: Query = {
    if (queries.isEmpty) throw new RuntimeException("Empty query is not allowed")
    Query.merge(queries.reverse)
  }
}

object QueryBuilderContext {
  private val threadLocal = new ThreadLocal[Option[QueryBuilderContext]] {
    override def initialValue(): Option[QueryBuilderContext] = None
  }

  def apply(): QueryBuilderContext = threadLocal.get().getOrElse(throw new RuntimeException(s"No QueryBuilderContext defined in current thread. Use `aql { ... }` around your query."))

  def contextualize(f: => Unit): Query = {
    clearRefs()
    val previous = threadLocal.get()
    try {
      val builder = new QueryBuilderContext
      threadLocal.set(Some(builder))
      f
      builder.toQuery
    } finally {
      if (previous.nonEmpty) {
        threadLocal.set(previous)
      } else {
        threadLocal.remove()
      }
    }
  }
}