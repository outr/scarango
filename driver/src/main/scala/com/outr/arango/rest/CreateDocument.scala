package com.outr.arango.rest

case class CreateDocument[T](_id: Option[String],
                             _key: Option[String],
                             _rev: Option[String],
                             `new`: Option[T])
