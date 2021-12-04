package com.outr.arango

case class DocumentRef[D <: Document[D], Model <: DocumentModel[D]](model: Model, refNameOverride: Option[String]) extends WrappedRef[Model](model, refNameOverride) {
  private val id = Unique()

  override def hashCode(): Int = id.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: DocumentRef[_, _] if that.id == this.id => true
    case _ => false
  }
}