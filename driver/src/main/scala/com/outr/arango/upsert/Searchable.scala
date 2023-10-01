package com.outr.arango.upsert

import com.outr.arango.Field
import com.outr.arango.query.{Query, QueryPart}

sealed trait Searchable {
  def toSearch: QueryPart
}

object Searchable {
  def apply[F](field1: Field[F], field2: Field[F]): Searchable = Filter[F](field1, field2)

  case class Filter[F](field1: Field[F], field2: Field[F]) extends Searchable {
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