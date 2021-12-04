package com.outr.arango.query.dsl

import com.outr.arango.query.Query
import com.outr.arango.{Document, DocumentModel, DocumentRef, ReadableCollection}

case class ForPartial[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]) {
  def IN(collection: ReadableCollection[D]): Unit = {
    val context = QueryBuilderContext()
    val name = context.name(ref)
    context.addQuery(Query(s"FOR $name IN ${collection.name}"))
  }
}
