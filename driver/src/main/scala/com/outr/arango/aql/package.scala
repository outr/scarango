package com.outr.arango

import io.youi.Unique

import scala.language.implicitConversions

package object aql extends AQLBuilder {
  private val referenceLocal = new ThreadLocal[Option[DocumentRef[_, _]]] {
    override def initialValue(): Option[DocumentRef[_, _]] = None
  }

//  implicit class DocumentModelExtras[D <: Document[D], Model <: DocumentModel[D]](dm: Model) {
//    def ref: DocumentRef[D, Model] = DocumentRef[D, Model](dm)
//  }

  implicit class FieldExtras[T](field: => Field[T]) {
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

  def withReference[Return](f: => Return): (Option[DocumentRef[_, _]], Return) = {
    referenceLocal.remove()
    try {
      val r = f
      (referenceLocal.get(), r)
    } finally {
      referenceLocal.remove()
    }
  }
}