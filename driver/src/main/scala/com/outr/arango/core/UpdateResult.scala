package com.outr.arango.core

case class UpdateResult(key: Option[String], id: Option[String], rev: Option[String], oldRev: Option[String], newDocument: Option[fabric.Value], oldDocument: Option[fabric.Value])