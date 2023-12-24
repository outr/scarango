package com.outr.arango.collection

import com.outr.arango.query.dsl._
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.{Document, DocumentModel, DocumentRef, Field}

class DocumentCollectionQuery[D <: Document[D]](collection: DocumentCollection[D]) extends QueryBuilder[D](
  graph = collection.graph,
  query = DocumentCollectionQuery.forCollection(collection),
  converter = collection.toT
) {
  /**
    * Creates a QueryBuilder from the supplied Query
    *
    * @param query the query to use
    * @return QueryBuilder[D]
    */
  def apply(query: Query): QueryBuilder[D] = new QueryBuilder[D](collection.graph, query, collection.toT)

  /**
    * Creates a QueryBuilder from the supplied filter and sorts the results
    *
    * @param filter the filter to apply to this collection
    * @param sort   the sorting to use
    * @param offset the offset to start
    * @param limit  the limit of results
    * @return QueryBuilder[D]
    */
  def byFilter[M <: DocumentModel[D]](filter: DocumentRef[D, M] => Filter,
               sort: Option[(Field[_], SortDirection)] = None,
               offset: Option[Int] = None,
               limit: Option[Int] = None): QueryBuilder[D] = noConsumingRefs {
    withRef[M] { d =>
      FOR(d) IN collection
      FILTER(filter(d))
      limit.foreach { l =>
        LIMIT(offset.getOrElse(0), l)
      }
      sort.map(_.asInstanceOf[(Field[Any], SortDirection)]).foreach(s => SORT(s))
      RETURN(d)
    }
  }

  /**
    * Creates a QueryBuilder all results with the sorts and limits
    *
    * @param sort   the sorting to use
    * @param offset the offset to start
    * @param limit  the limit of results
    * @return QueryBuilder[D]
    */
  def all(sort: Option[(Field[_], SortDirection)] = None,
          offset: Option[Int] = None,
          limit: Option[Int] = None): QueryBuilder[D] = noConsumingRefs {
    withRef[DocumentModel[D]] { d =>
      FOR(d) IN collection
      limit.foreach { l =>
        LIMIT(offset.getOrElse(0), l)
      }
      sort.map(_.asInstanceOf[(Field[Any], SortDirection)]).foreach(s => SORT(s))
      RETURN(d)
    }
  }

  /**
    * Receives a DocumentRef in order to create a DSL query.
    *
    * @param f the function to create the query
    * @return QueryBuilder[D]
    */
  def withRef[M <: DocumentModel[D]](f: DocumentRef[D, M] => Unit): QueryBuilder[D] = try {
    val d: DocumentRef[D, M] = DocumentRef[D, M](collection.model.asInstanceOf[M], Some("d"))
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
  def forCollection[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D]): Query = try {
    val d: DocumentRef[D, M] = DocumentRef(collection.model.asInstanceOf[M], Some("d"))
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