package com.outr.arango.collection

import com.outr.arango.{Document, DocumentModel, DocumentRef, Field}
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.query.dsl._

class DocumentCollectionQuery[D <: Document[D]](collection: DocumentCollection[D]) extends QueryBuilder[D](
  graph = collection.graph,
  query = DocumentCollectionQuery.forCollection(collection),
  converter = collection.toT
) with DocumentQuery[D] {
  override def apply(query: Query): QueryBuilder[D] = new QueryBuilder[D](collection.graph, query, collection.toT)

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

object DocumentCollectionQuery {
  def forCollection[D <: Document[D]](collection: DocumentCollection[D]): Query = {
    val d: DocumentRef[D, DocumentModel[D]] = DocumentRef(collection.model, Some("d"))
    aql {
      withReference(d) {
        FOR(d) IN collection
        RETURN(d)
      }
    }
  }
}