package com.outr.arango.query.dsl

import com.outr.arango.collection.Collection
import com.outr.arango.query.{Query, QueryPart}
import com.outr.arango.{Document, DocumentModel, DocumentRef}

case class RemovePartial[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]) {
  def IN(collection: Collection): Unit = {
    val context = QueryBuilderContext()
    context.addQuery(Query(List(
      QueryPart.Static("REMOVE "),
      QueryPart.Ref(ref),
      QueryPart.Static(s" IN ${collection.name}")
    )))
  }
}
