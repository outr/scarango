package com.outr.arango.collection

import com.outr.arango._
import com.outr.arango.core.{ArangoDBCollection, CollectionSchema, ComputedValue}
import fabric.Json

class DocumentCollection[D <: Document[D], M <: DocumentModel[D]](protected[arango] val graph: Graph,
                                                                  protected[arango] val arangoCollection: ArangoDBCollection,
                                                                  val model: M,
                                                                  val `type`: CollectionType,
                                                                  val managed: Boolean) extends WritableCollection[D, M] {
  override def dbName: String = graph.databaseName

  override def name: String = arangoCollection.name

  override lazy val query: DocumentCollectionQuery[D, M] = new DocumentCollectionQuery[D, M](this)

  override protected def beforeStorage(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.store(v))

  override protected def afterRetrieval(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.retrieve(v))

  def ref: DocumentRef[D, M] = DocumentRef[D, M](model, None)

  lazy val update: UpdateBuilder[D, M] = UpdateBuilder(this)
  lazy val upsert: UpsertBuilder[D, M] = UpsertBuilder(this)
}