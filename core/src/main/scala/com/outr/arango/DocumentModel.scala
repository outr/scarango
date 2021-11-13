package com.outr.arango

import com.outr.arango.core.CreateCollectionOptions
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

  def collectionOptions: CreateCollectionOptions = CreateCollectionOptions()

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
