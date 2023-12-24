package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango._
import com.outr.arango.query.dsl._
import com.outr.arango.query.{Query, QueryPart}
import fabric.rw._

case class UpdateBuilder[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D],
                                                                  ignoreErrors: Boolean = false,
                                                                  keepNull: Boolean = true,
                                                                  mergeObjects: Boolean = true,
                                                                  waitForSync: Boolean = false,
                                                                  ignoreRevs: Boolean = true,
                                                                  exclusive: Boolean = false,
                                                                  refillIndexCaches: Boolean = false) {
  type Update = DocumentRef[D, M] => (Filter, List[(Field[_], Query)])

  def withOptions(ignoreErrors: Boolean = false,
                  keepNull: Boolean = true,
                  mergeObjects: Boolean = true,
                  waitForSync: Boolean = false,
                  ignoreRevs: Boolean = true,
                  exclusive: Boolean = false,
                  refillIndexCaches: Boolean = false): UpdateBuilder[D, M] = copy(
    ignoreErrors = ignoreErrors,
    keepNull = keepNull,
    mergeObjects = mergeObjects,
    waitForSync = waitForSync,
    ignoreRevs = ignoreRevs,
    exclusive = exclusive,
    refillIndexCaches = refillIndexCaches
  )

  def toQuery(f: Update,
              applyReturn: => Unit): Query = noConsumingRefs {
    val v = collection.ref

    def opt(name: String, value: Boolean, default: Boolean): Option[Query] = if (value != default) {
      Some(Query.static(s"$name: $value"))
    } else {
      None
    }

    aql {
      FOR(v) IN collection
      val (filter, queries) = f(v)
      FILTER(filter)
      val modifiers = Query.merge(queries.map {
        case (field, query) => Query.merge(List(
          Query.static("'"),
          Query.static(field.fullyQualifiedName),
          Query.static("': "),
          query
        ), "")
      }, ", ")
      val updateQueries = List(
        Query("UPDATE "),
        Query(List(QueryPart.Ref(v))),
        Query.static(" WITH {"),
        modifiers,
        Query.static("} IN "),
        Query.static(collection.name),
        Query.static(" OPTIONS {"),
        Query.merge(List(
          opt("ignoreErrors", ignoreErrors, default = false), opt("keepNull", keepNull, default = true),
          opt("mergeObjects", mergeObjects, default = true), opt("waitForSync", waitForSync, default = false),
          opt("ignoreRevs", ignoreRevs, default = true), opt("exclusive", exclusive, default = false),
          opt("refillIndexCaches", refillIndexCaches, default = false)
        ).flatten, ", "),
        Query.static("}")
      )
      addQuery(Query.merge(updateQueries, ""))
      applyReturn
    }
  }

  def apply(f: Update): IO[Int] = {
    val count = NamedRef("count")
    val query = toQuery(f, {
      COLLECT WITH COUNT INTO count
      RETURN(count)
    })
    collection.graph.query[Int](query).one
  }

  def toStream(f: Update): fs2.Stream[IO, D] = {
    val query = toQuery(f, RETURN(NEW))
    collection.query(query).stream()
  }

  def toList(f: Update): IO[List[D]] = toStream(f).compile.toList
}
