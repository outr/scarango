package com.outr.arango.query

import com.outr.arango.{Collection, Document, DocumentModel, DocumentRef, Query}

case class RemovePartial[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]) {
  def IN(collection: Collection[D]): Unit = {
    val context = QueryBuilderContext()
    val name = context.name(ref)
    context.addQuery(Query(
      value = s"REMOVE $name IN ${collection.name}",
      args = Map.empty
    ))
  }
}