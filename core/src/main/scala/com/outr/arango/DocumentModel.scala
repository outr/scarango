package com.outr.arango

import io.youi.Unique

trait DocumentModel[D <: Document[D]] {
  def collectionName: String
  implicit def serialization: Serialization[D]

  protected def generateId(): String = Unique()

  def id(value: String = generateId(),
         revision: Option[String] = None): Id[D] = Id[D](value, collectionName, revision)
}