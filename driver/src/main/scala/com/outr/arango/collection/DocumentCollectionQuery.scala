package com.outr.arango.collection

import com.outr.arango.{Document, DocumentModel, DocumentRef, Field}
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.query.dsl._

class DocumentCollectionQuery[D <: Document[D]](collection: DocumentCollection[D]) extends DocumentQuery[D] {
  override def apply(query: Query): QueryBuilder[D] = QueryBuilder[D](collection.graph, query, collection.model.rw)

  override def byFilter(filter: => Filter): QueryBuilder[D] = {
    val d: DocumentRef[D, DocumentModel[D]] = DocumentRef(collection.model, Some("d"))
    apply(aql {
      withReference(d) {
        FOR(d) IN collection
        FILTER(filter)
        RETURN(d)
      }
    })
  }

  override def byFilter(filter: => Filter, sort:  (Field[_], SortDirection)): QueryBuilder[D] = {
    val d: DocumentRef[D, DocumentModel[D]] = DocumentRef(collection.model, Some("d"))
    apply(aql {
      withReference(d) {
        FOR(d) IN collection
        FILTER(filter)
        SORT(sort.asInstanceOf[(Field[Any], SortDirection)])
        RETURN(d)
      }
    })
  }
}