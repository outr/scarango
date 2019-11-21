package com.outr.arango.aql

import java.util.concurrent.atomic.AtomicInteger

import com.outr.arango.{NamedRef, Query, Ref}

class QueryBuilderContext private() {
  private var queries = List.empty[Query]
  private var refNames = Map.empty[Ref, String]
  private lazy val incrementer = new AtomicInteger(0)

  var ref: Option[Ref] = None

  def addQuery(query: Query): Unit = queries = query :: queries

  def name(ref: Ref): String = ref match {
    case NamedRef(name) => name
    case _ => {
      refNames.get(ref) match {
        case Some(name) => name
        case None => {
          val name = createArg
          refNames += ref -> name
          name
        }
      }
    }
  }

  def createArg: String = s"arg${incrementer.incrementAndGet()}"

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