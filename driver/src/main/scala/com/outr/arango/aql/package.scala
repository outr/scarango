package com.outr.arango

import io.youi.Unique

import scala.language.experimental.macros
import scala.language.implicitConversions

package object aql {
  private val referenceLocal = new ThreadLocal[Option[DocumentRef[_, _]]] {
    override def initialValue(): Option[DocumentRef[_, _]] = None
  }
  private var forced = false

  implicit def ref2ReturnPart[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ReturnPart = {
    DocumentRefReturnPart(ref)
  }

  implicit class FieldExtras[T](field: => Field[T]) {
    def thisField: Field[T] = field

    def apply(value: T): FieldAndValue[T] = macro AQLMacros.fieldAndValue
    private def cond(value: T, condition: String)(implicit conversion: T => Value): Filter = {
      val (refOption, f) = withReference(field)
      val ref = refOption.getOrElse(throw new RuntimeException("No reference for field!"))
      val left = (b: AQLBuilder) => {
        val name = b.createName(ref.model.asInstanceOf[DocumentModel[_]].collectionName, ref.id, 1)
        Query(s"$name.${f.name}", Map.empty)
      }
      val right = (b: AQLBuilder) => {
        val name = b.createName("arg", Unique(), 1)
        Query(s"@$name", Map(name -> conversion(value)))
      }
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

    def asc: (Field[T], SortDirection) = (field, SortDirection.ASC)
    def ASC: (Field[T], SortDirection) = (field, SortDirection.ASC)
    def desc: (Field[T], SortDirection) = (field, SortDirection.DESC)
    def DESC: (Field[T], SortDirection) = (field, SortDirection.DESC)
  }

  implicit def documentRef2DocumentModel[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): Model = {
    referenceLocal.set(Some(ref))
    ref.model
  }

  def FOR[D <: Document[D], Model <: DocumentModel[D]](ref: DocumentRef[D, Model]): ForPartial[D, Model] = {
    new AQLBuilder().FOR(ref)
  }

  def NEW: ReturnPart = NewReturnPart

  def withReference[D <: Document[D], Model <: DocumentModel[D], Return](ref: DocumentRef[D, Model])(f: => Return): Return = {
    referenceLocal.set(Some(ref))
    forced = true
    try {
      val r: Return = f
      r
    } finally {
      referenceLocal.remove()
      forced = false
    }
  }

  def withReference[Return](f: => Return): (Option[DocumentRef[_, _]], Return) = {
    if (!forced) referenceLocal.remove()
    try {
      val r = f
      (referenceLocal.get(), r)
    } finally {
      referenceLocal.remove()
    }
  }
}