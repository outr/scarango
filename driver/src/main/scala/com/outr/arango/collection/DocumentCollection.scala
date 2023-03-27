package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.core.ArangoDBCollection
import com.outr.arango.query.dsl._
import com.outr.arango._
import fabric.Json

class DocumentCollection[D <: Document[D]](protected[arango] val graph: Graph,
                                           protected[arango] val arangoCollection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType,
                                           val managed: Boolean) extends WritableCollection[D] {
  override def dbName: String = graph.databaseName
  override def name: String = arangoCollection.name
  override lazy val query: DocumentCollectionQuery[D] = new DocumentCollectionQuery[D](this)

  override protected def beforeStorage(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.store(v))

  override protected def afterRetrieval(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.retrieve(v))

  def modify(filter: => Filter, fieldAndValues: FieldAndValue[_]*): IO[Int] = {
    val v = DocumentRef[D, DocumentModel[D]](model, None)
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
}