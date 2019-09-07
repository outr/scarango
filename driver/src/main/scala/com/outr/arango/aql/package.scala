package com.outr.arango

import scala.language.implicitConversions

package object aql extends AQLBuilder {
  private val referenceLocal = new ThreadLocal[Option[DocumentRef[_, _]]] {
    override def initialValue(): Option[DocumentRef[_, _]] = None
  }

//  implicit class DocumentModelExtras[D <: Document[D], Model <: DocumentModel[D]](dm: Model) {
//    def ref: DocumentRef[D, Model] = DocumentRef[D, Model](dm)
//  }

  implicit class FieldExtras[T](field: Field[T]) {
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