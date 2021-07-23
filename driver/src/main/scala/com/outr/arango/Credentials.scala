package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class Credentials(username: String, password: String)

object Credentials {
  implicit val rw: ReaderWriter[Credentials] = ccRW
}