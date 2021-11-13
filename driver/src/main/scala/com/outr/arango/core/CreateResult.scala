package com.outr.arango.core

case class CreateResult[T](key: Option[String],
                           id: Option[String],
                           rev: Option[String],
                           newDocument: Option[T],
                           oldDocument: Option[T])