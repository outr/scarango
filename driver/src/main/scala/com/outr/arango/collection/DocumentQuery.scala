package com.outr.arango.collection

import com.outr.arango.{Document, Field}
import com.outr.arango.query.{Query, SortDirection}
import com.outr.arango.query.dsl.Filter

trait DocumentQuery[D <: Document[D]] {
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
  def byFilter(filter: => Filter): QueryBuilder[D]

  /**
    * Creates a QueryBuilder from the supplied filter and sorts the results
    * @param filter the filter to apply to this collection
    * @param sort the sorting to use
    * @return QueryBuilder[D]
    */
  def byFilter(filter: => Filter, sort: (Field[_], SortDirection)): QueryBuilder[D]
}