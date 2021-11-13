package com.outr.arango.core

case class DeleteResult[T](key: Option[String], id: Option[String], rev: Option[String], oldDocument: Option[T])