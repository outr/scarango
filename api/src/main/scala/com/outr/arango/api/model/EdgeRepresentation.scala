package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class EdgeRepresentation(From: Option[String] = None,
                              Id: Option[String] = None,
                              Key: Option[String] = None,
                              Rev: Option[String] = None,
                              To: Option[String] = None)

object EdgeRepresentation {
  implicit val rw: ReaderWriter[EdgeRepresentation] = ccRW
}