package com.outr.arango.query.dsl

import com.outr.arango.collection.Collection
import com.outr.arango.query.Query
import com.outr.arango.{Document, DocumentModel, DocumentRef}

case class RemovePartial[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]) {
  def IN(collection: Collection): Unit = {
    val context = QueryBuilderContext()
    val name = context.name(ref)
    context.addQuery(Query(s"REMOVE $name IN ${collection.name}"))
  }
}
