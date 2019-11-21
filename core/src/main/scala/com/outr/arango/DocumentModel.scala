package com.outr.arango

import io.youi.Unique

trait DocumentModel[D <: Document[D]] {
  val collectionName: String
  implicit val serialization: Serialization[D]

  protected def generateId(): String = Unique()

  object index {
    def apply(fields: Field[_]*): List[Index] = fields.map(_.index.persistent()).toList
    def unique(fields: Field[_]*): List[Index] = fields.map(_.index.persistent(unique = true)).toList
  }

  def indexes: List[Index]

  def id(value: String = generateId()): Id[D] = if (value.startsWith(collectionName)) {
    Id[D](value.substring(collectionName.length + 1), collectionName)
  } else {
    Id[D](value, collectionName)
  }
}

trait Ref {
  def id: String
}

case class DocumentRef[D <: Document[D], Model <: DocumentModel[D]](model: Model, id: String = Unique()) extends Ref