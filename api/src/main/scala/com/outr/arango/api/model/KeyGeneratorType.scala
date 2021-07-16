package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class KeyGeneratorType(allowUserKeys: Option[Boolean] = None,
                            lastValue: Option[Int] = None,
                            `type`: Option[String] = None)

object KeyGeneratorType {
  implicit val rw: ReaderWriter[KeyGeneratorType] = ccRW
}