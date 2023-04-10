package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango._
import com.outr.arango.core.ArangoDBCollection
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

  lazy val update: UpdateBuilder[D, M] = UpdateBuilder(this)
}