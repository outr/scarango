package com.outr.arango.core

case class CreateResult[T](key: Option[String],
                           id: Option[String],
                           rev: Option[String],
                           newDocument: Option[T],
                           oldDocument: Option[T]) {
  def convert[S](converter: T => S): CreateResult[S] = copy(
    newDocument = newDocument.map(converter),
    oldDocument = oldDocument.map(converter)
  )
}