package com.outr.arango.query

import com.outr.arango.{Document, DocumentModel, DocumentRef, Field, FieldAndValue, NamedRef, Ref, WrappedRef}
import fabric._
import fabric.rw._

import scala.language.implicitConversions

package object dsl {
  private val forced = new ThreadLocal[Boolean] {
    override def initialValue(): Boolean = false
  }

  implicit def int2Value(i: Int): Json = num(i)

  implicit def string2Json(s: String): Json = str(s)

  implicit def ref2ReturnPart(ref: Ref): ReturnPart = {
    ReturnPart.RefReturn(ref)
  }

  implicit def string2ReturnPart(json: String): ReturnPart = this.json(json)

  implicit class ValueExtras[T](value: T) {
    def IN(field: Field[T]): Filter = {
      val context = QueryBuilderContext()
      val (refOption, f) = withReference(field)
      val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
      val leftName = context.name(ref)
      new Filter(Query(List(QueryPart.Variable(value.json(field.rw)))), "IN", Query(s"$leftName.${f.fieldName}"))
    }
  }

  def withReference[Return](ref: Ref)(f: => Return): Return = {
    val context = QueryBuilderContext()
    context.ref = Some(ref)
    val previous = forced.get()
    forced.set(true)
    try {
      val r: Return = f
      r
    } finally {
      context.ref = None
      forced.set(previous)
    }
  }

  def withReference[Return](f: => Return): (Option[Ref], Return) = {
    val context = QueryBuilderContext()
    val r: Return = f
    (context.ref, r)
  }

  implicit def ref2Wrapped[T](ref: WrappedRef[T]): T = {
    val context = QueryBuilderContext()
    context.ref = Some(ref)
    ref.wrapped
  }

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = ForPartial(ref)

  def REMOVE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): RemovePartial[D, Model] = RemovePartial(ref)

  def SORT[T](f: => (Field[T], SortDirection)): Unit = {
    val context = QueryBuilderContext()
    val (refOption, (field, sort)) = withReference(f)
    val ref = refOption.getOrElse(throw new RuntimeException("No ref option found for SORT!"))
    val name = context.name(ref)
    val sortValue = sort match {
      case SortDirection.ASC => "ASC"
      case SortDirection.DESC => "DESC"
    }
    context.addQuery(Query(s"SORT $name.${field.fieldName} $sortValue"))
  }

  def FILTER(filter: Filter): Unit = {
    val query = filter.build()
    addQuery(Query.merge(List(Query("FILTER"), query), " "))
  }

  def COLLECT: CollectStart.type = CollectStart

  def COUNT: CollectWith.Count.type = CollectWith.Count

  def UPDATE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: FieldAndValue[_]*): Unit = {
    addQuery(UpdatePart(ref, values.toList).build())
  }

  def NEW: ReturnPart = ReturnPart.New

  def mapped(mappings: (String, Field[_])*): ReturnPart = json(mappings.map {
    case (name, field) => s"$name: ${field.fieldName}"
  }.mkString("{", ", ", "}"))

  def json(json: String): ReturnPart = ReturnPart.Json(json)

  def LIMIT(offset: Int, limit: Int): Unit = addQuery(Query(s"LIMIT $offset, $limit"))

  def LIMIT(limit: Int): Unit = LIMIT(0, limit)

  def RETURN(part: ReturnPart): Unit = addQuery(part.build())

  def RETURN[T](field: => Field[T]): Unit = {
    val context = QueryBuilderContext()
    val (refOption, f) = withReference(field)
    val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
    val leftName = context.name(ref)
    addQuery(Query(s"RETURN $leftName.${f.fieldName}"))
  }

  def addQuery(query: Query): Unit = {
    val context = QueryBuilderContext()
    context.addQuery(query)
  }

  def ref: Ref = new Ref {
    override def refName: Option[String] = None
  }

  def ref(name: String): Ref = NamedRef(name)

  def ref[T](wrapped: T, name: Option[String]): WrappedRef[T] = new WrappedRef[T](wrapped, name)

  def aql(f: => Unit): Query = QueryBuilderContext.contextualize(f)
}