package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango._
import com.outr.arango.core.ArangoDBCollection
import com.outr.arango.query.Query
import com.outr.arango.query.dsl._
import fabric.Json

class DocumentCollection[D <: Document[D], M <: DocumentModel[D]](protected[arango] val graph: Graph,
                                                                  protected[arango] val arangoCollection: ArangoDBCollection,
                                                                  val model: M,
                                                                  val `type`: CollectionType,
                                                                  val managed: Boolean) extends WritableCollection[D] {
  override def dbName: String = graph.databaseName

  override def name: String = arangoCollection.name

  override lazy val query: DocumentCollectionQuery[D, M] = new DocumentCollectionQuery[D, M](this)

  override protected def beforeStorage(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.store(v))

  override protected def afterRetrieval(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.retrieve(v))

  @deprecated(message = "Use updateWith instead", since = "3.10")
  def modify(filter: => Filter, fieldAndValues: FieldAndValue[_]*): IO[Int] = {
    val v = ref
    val count = NamedRef("count")

    val query = aql {
      FOR(v) IN this
      FILTER(withReference(v)(filter))
      UPDATE(v, fieldAndValues: _*)
      COLLECT WITH COUNT INTO count
      RETURN(count)
    }
    graph.query[Int](query).one
  }

  def ref: DocumentRef[D, M] = DocumentRef[D, M](model, None)

  def update(f: DocumentRef[D, M] => (Filter, List[(Field[_], Query)])): IO[Int] = updateWithOptions()(f)

  def updateWithOptions(ignoreErrors: Boolean = false,
                        keepNull: Boolean = true,
                        mergeObjects: Boolean = true,
                        waitForSync: Boolean = false,
                        ignoreRevs: Boolean = true,
                        exclusive: Boolean = false,
                        refillIndexCaches: Boolean = false)
                       (f: DocumentRef[D, M] => (Filter, List[(Field[_], Query)])): IO[Int] = {
    val v = ref
    val count = NamedRef("count")

    def opt(name: String, value: Boolean, default: Boolean): Option[Query] = if (value != default) {
      Some(Query.static(s"$name: $value"))
    } else {
      None
    }

    val query = aql {
      FOR(v) IN this
      val (filter, queries) = f(v)
      FILTER(filter)
      val context = QueryBuilderContext()
      val refName = context.name(v)
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
        Query.static(refName),
        Query.static(" WITH {"),
        modifiers,
        Query.static("} IN "),
        Query.static(name),
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
      COLLECT WITH COUNT INTO count
      RETURN(count)
    }
    graph.query[Int](query).one
  }
}