package com.outr.arango.query.dsl

import com.outr.arango.Ref
import com.outr.arango.query.{Query, QueryPart}

sealed trait ReturnPart {
  def value: QueryPart

  def build(): Query = Query(List(
    QueryPart.Static("RETURN "),
    value
  ))
}

object ReturnPart {
  case class RefReturn(ref: Ref) extends ReturnPart {
    override def value: QueryPart = QueryPart.Ref(ref)
  }

  case class Json(jsonString: String) extends ReturnPart {
    override def value: QueryPart = QueryPart.Static(jsonString)
  }

  object New extends ReturnPart {
    override val value: QueryPart = QueryPart.Static("NEW")
  }
}