package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class Warning(code: Int, message: String)

object Warning {
  implicit val rw: ReaderWriter[Warning] = ccRW
}