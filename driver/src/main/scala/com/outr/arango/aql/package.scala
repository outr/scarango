package com.outr.arango

import io.youi.Unique

import scala.language.experimental.macros
import scala.language.implicitConversions

package object aql {
  private var forced = false

  /*private val referenceLocal = new ThreadLocal[Option[DocumentRef[_, _]]] {
    override def initialValue(): Option[DocumentRef[_, _]] = None
  }

  implicit def ref2ReturnPart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ReturnPart = {
    DocumentRefReturnPart(ref)
  }

  implicit def documentRef2DocumentModel[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): Model = {
    referenceLocal.set(Some(ref))
    ref.model
  }

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = {
    new AQLBuilder().FOR(ref)
  }*/

  implicit def ref2ReturnPart(ref: Ref): ReturnPart = {
    ReturnPart.RefReturn(ref)
  }

  implicit class FieldExtras[T](field: => Field[T]) {
    def thisField: Field[T] = field

    def apply(value: T): FieldAndValue[T] = macro AQLMacros.fieldAndValue
    private def cond(value: T, condition: String)(implicit conversion: T => Value): Filter = {
      val context = QueryBuilderContext()
      val (refOption, f) = withReference(field)
      val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
      val leftName = context.name(ref)
      val rightName = context.createArg
      val left = Query(s"$leftName.${f.name}", Map.empty)
      val right = Query(s"@$rightName", Map(rightName -> conversion(value)))

      new Filter(left, condition, right)
    }
    def is(value: T)(implicit conversion: T => Value): Filter = ===(value)
    def ===(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, "==")
    }
    def isNot(value: T)(implicit conversion: T => Value): Filter = !==(value)
    def !=(value: T)(implicit conversion: T => Value): Filter = !==(value)
    def !==(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, "!=")
    }
    def >=(value: T)(implicit conversion: T => Value): Filter = {
      cond(value, ">=")
    }

    def asc: (Field[T], SortDirection) = (field, SortDirection.ASC)
    def ASC: (Field[T], SortDirection) = (field, SortDirection.ASC)
    def desc: (Field[T], SortDirection) = (field, SortDirection.DESC)
    def DESC: (Field[T], SortDirection) = (field, SortDirection.DESC)
  }

  def withReference[D <: Document[D], Model <: DocumentModel[D], Return](ref: DocumentRef[D, Model])(f: => Return): Return = {
    val context = QueryBuilderContext()
    context.documentRef = Some(ref)
    forced = true
    try {
      val r: Return = f
      r
    } finally {
      context.documentRef = None
      forced = false
    }
  }

  def withReference[Return](f: => Return): (Option[DocumentRef[_, _]], Return) = {
    val context = QueryBuilderContext()
    if (!forced) context.documentRef = None
    try {
      val r = f
      (context.documentRef, r)
    } finally {
      context.documentRef = None
    }
  }

  implicit def documentRef2DocumentModel[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): Model = {
    val context = QueryBuilderContext()
    context.documentRef = Some(ref)
    ref.model
  }

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = ForPartial(ref)

  def SORT[T](f: => (Field[T], SortDirection)): Unit = {
    val context = QueryBuilderContext()
    val (refOption, (field, sort)) = withReference(f)
    val ref = refOption.getOrElse(throw new RuntimeException("No ref option found for SORT!"))
    val name = context.name(ref)
    val sortValue = sort match {
      case SortDirection.ASC => "ASC"
      case SortDirection.DESC => "DESC"
    }
    context.addQuery(Query(s"SORT $name.${field.name} $sortValue", Map.empty))
  }

  def FILTER(filter: Filter): Unit = {
    val query = filter.build()
    add(query.copy(value = s"FILTER ${query.value}"))
  }

  def COLLECT: CollectStart.type = CollectStart

  def COUNT: CollectWith.Count.type = CollectWith.Count

  def UPDATE[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model], values: FieldAndValue[_]*): Unit = {
    add(UpdatePart(ref, values.toList).build())
  }

  def NEW: ReturnPart = ReturnPart.New

  def RETURN(part: ReturnPart): Unit = add(part.build())

  private def add(query: Query): Unit = {
    val context = QueryBuilderContext()
    context.addQuery(query)
  }

  def ref: Ref = new Ref
  def ref(name: String): Ref = NamedRef(name)

  def aql(f: => Unit): Query = QueryBuilderContext.contextualize(f)
}

/*
case class SortPart[T](f: () => (Field[T], SortDirection)) extends QueryPart {
  override def build(builder: AQLBuilder): Query = {
    val (refOption, (field, sort)) = withReference(f())
    val ref = refOption.getOrElse(throw new RuntimeException("No ref option found for SORT!"))
    val name = builder.createName(ref.model.asInstanceOf[DocumentModel[_]].collectionName, ref.id, 1)
    val sortValue = sort match {
      case SortDirection.ASC => "ASC"
      case SortDirection.DESC => "DESC"
    }
    Query(s"SORT $name.${field.name} $sortValue", Map.empty)
  }
}
 */