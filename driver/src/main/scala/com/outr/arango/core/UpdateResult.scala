package com.outr.arango.core

case class UpdateResult[T](key: Option[String], id: Option[String], rev: Option[String], oldRev: Option[String], newDocument: Option[T], oldDocument: Option[T])