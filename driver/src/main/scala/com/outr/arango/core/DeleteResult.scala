package com.outr.arango.core

case class DeleteResult(key: Option[String], id: Option[String], rev: Option[String], oldDocument: Option[fabric.Value])