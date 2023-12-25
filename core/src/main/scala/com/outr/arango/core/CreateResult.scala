package com.outr.arango.core

case class CreateResult[T](key: Option[String],
                           id: Option[String],
                           rev: Option[String],
                           document: T,
                           newDocument: Option[T],
                           oldDocument: Option[T]) {
  def convert[S](converter: T => S): CreateResult[S] = copy(
    document = converter(document),
    newDocument = newDocument.map(converter),
    oldDocument = oldDocument.map(converter)
  )
}