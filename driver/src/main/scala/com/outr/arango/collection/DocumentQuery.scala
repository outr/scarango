package com.outr.arango.collection

import com.outr.arango.query.dsl.Filter
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.{Document, DocumentModel, DocumentRef, Field}

trait DocumentQuery[D <: Document[D], M <: DocumentModel[D]] {
  /**
    * Creates a QueryBuilder from the supplied Query
    * @param query the query to use
    * @return QueryBuilder[D]
    */
  def apply(query: Query): QueryBuilder[D]

  /**
    * Creates a QueryBuilder from the supplied filter
    * @param filter the filter to apply to this collection
    * @return QueryBuilder[D]
    */
  def byFilter(filter: DocumentRef[D, M] => Filter): QueryBuilder[D]

  /**
    * Creates a QueryBuilder from the supplied filter and sorts the results
    * @param filter the filter to apply to this collection
    * @param sort the sorting to use
    * @return QueryBuilder[D]
    */
  def byFilter(filter: DocumentRef[D, M] => Filter, sort: (Field[_], SortDirection)): QueryBuilder[D]

  /**
    * Receives a DocumentRef in order to create a DSL query.
    *
    * @param f the function to create the query
    * @return QueryBuilder[D]
    */
  def withRef(f: DocumentRef[D, M] => Unit): QueryBuilder[D]
}