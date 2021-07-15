package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class AdminEchoClientStruct()

object AdminEchoClientStruct {
  implicit val rw: ReaderWriter[AdminEchoClientStruct] = ccRW
}