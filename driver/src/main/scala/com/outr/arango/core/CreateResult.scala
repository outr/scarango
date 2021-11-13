package com.outr.arango.core

case class CreateResult(key: Option[String],
                        id: Option[String],
                        rev: Option[String],
                        newDocument: Option[fabric.Value],
                        oldDocument: Option[fabric.Value])