package com.outr.arango.query.dsl

import com.outr.arango.Ref
import com.outr.arango.query.Query

sealed trait ReturnPart {
  def value: String

  def build(): Query = {
    Query(s"RETURN $value")
  }
}

object ReturnPart {
  case class RefReturn(ref: Ref) extends ReturnPart {
    override def value: String = {
      val context = QueryBuilderContext()
      val name = context.name(ref)
      name
    }
  }

  case class Json(override val value: String) extends ReturnPart

  object New extends ReturnPart {
    override val value: String = "NEW"
  }
}