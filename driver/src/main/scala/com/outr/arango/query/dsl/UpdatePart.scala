package com.outr.arango.query.dsl

import com.outr.arango.query.{Query, QueryPart}
import com.outr.arango.{Document, DocumentModel, DocumentRef, FieldAndValue}

case class UpdatePart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: List[FieldAndValue[_]]) {
  def build(): Query = {
    val context = QueryBuilderContext()
    val name = context.name(ref)
    val fields = Query.merge(values.map { fv =>
      Query
        .static(", ")
        .static(s"  ${fv.field.fieldName}: ")
        .variable(fv.value)
    }) match {
      case q => q.copy(q.parts.tail)
    }
    val pre = Query.static(s"UPDATE $name WITH {")
    val post = Query.static(s"} IN ${ref.collectionName}")
    Query.merge(List(pre, fields, post))
  }
}