package com.outr.arango.core

import fabric.rw._

case class Host(host: String = "127.0.0.1", port: Int = 8529)

object Host {
  implicit val rw: RW[Host] = ccRW
}