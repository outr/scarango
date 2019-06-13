package com.outr.arango

import io.youi.Unique

trait DocumentModel[D <: Document[D]] {
  val collectionName: String
  implicit val serialization: Serialization[D]

  protected def generateId(): String = Unique()

  def id(value: String = generateId(),
         revision: Option[String] = None): Id[D] = Id[D](value, collectionName, revision)
}