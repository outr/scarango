package com.outr.arango.collection

import com.outr.arango.query.dsl._
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.{Document, DocumentModel, DocumentRef, Field}

class DocumentCollectionQuery[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M]) extends QueryBuilder[D](
  graph = collection.graph,
  query = DocumentCollectionQuery.forCollection(collection),
  converter = collection.toT
) with DocumentQuery[D, M] {
  override def apply(query: Query): QueryBuilder[D] = new QueryBuilder[D](collection.graph, query, collection.toT)

  override def byFilter(filter: DocumentRef[D, M] => Filter): QueryBuilder[D] = noConsumingRefs {
    withRef { d =>
      FOR(d) IN collection
      FILTER(filter(d))
      RETURN(d)
    }
  }

  override def byFilter(filter: DocumentRef[D, M] => Filter,
                        sort: (Field[_], SortDirection)): QueryBuilder[D] = noConsumingRefs {
    withRef { d =>
      FOR(d) IN collection
      FILTER(filter(d))
      SORT(sort.asInstanceOf[(Field[Any], SortDirection)])
      RETURN(d)
    }
  }

  override def withRef(f: DocumentRef[D, M] => Unit): QueryBuilder[D] = try {
    val d: DocumentRef[D, M] = DocumentRef(collection.model, Some("d"))
    apply(aql {
      withReference(d) {
        f(d)
      }
    })
  } finally {
    clearRefs()
  }
}

object DocumentCollectionQuery {
  def forCollection[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M]): Query = try {
    val d: DocumentRef[D, DocumentModel[D]] = DocumentRef(collection.model, Some("d"))
    aql {
      withReference(d) {
        FOR(d) IN collection
        RETURN(d)
      }
    }
  } finally {
    clearRefs()
  }
}