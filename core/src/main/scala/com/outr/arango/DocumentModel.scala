package com.outr.arango

import io.youi.Unique
import fabric.rw.ReaderWriter

trait DocumentModel[D <: Document[D]] {
  val collectionName: String

  implicit val rw: ReaderWriter[D]

  protected def generateId(): String = Unique()

  protected def field[T](name: String): Field[T] = Field[T](name)

  object index {
    def apply(fields: Field[_]*): List[Index] = fields.map(_.index.persistent()).toList
    def unique(fields: Field[_]*): List[Index] = fields.map(_.index.persistent(unique = true)).toList
  }

  def indexes: List[Index]

  def id(value: String = generateId()): Id[D] = {
    val index = value.indexOf('/')
    val v = if (index != -1) {
      value.substring(index + 1)
    } else {
      value
    }
    Id[D](v, collectionName)
  }
}

trait Ref {
  def refName: Option[String]
}

case class NamedRef(name: String) extends Ref {
  lazy val refName: Option[String] = Some(name)
}

object NamedRef {
  def apply(): NamedRef = NamedRef(s"$$ref_${Unique(length = 8)}")
}

class WrappedRef[T](val wrapped: T, val refName: Option[String] = None) extends Ref

case class DocumentRef[D <: Document[D], Model <: DocumentModel[D]](model: Model, refNameOverride: Option[String]) extends WrappedRef[Model](model, refNameOverride) {
  private val id = Unique()

  override def hashCode(): Int = id.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: DocumentRef[_, _] if that.id == this.id => true
    case _ => false
  }
}