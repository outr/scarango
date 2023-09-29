package com.outr.arango.upsert

import com.outr.arango.Field
import com.outr.arango.query.{Query, QueryPart}

object Searchable {
  case class Filter[F](field1: Field[F], condition: String, field2: Field[F]) extends Searchable {
    override val toSearch: QueryPart = Query.merge(
      List(
        Query(field1.fieldName),
        Query(":"),
        Query(List(field2.fqfPart))
      ),
      separator = " "
    )
  }
}

sealed trait Searchable {
  def toSearch: QueryPart
}