package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class AdminEchoServerStruct(address: Option[Int] = None,
                                 id: Option[String] = None,
                                 port: Option[Int] = None)

object AdminEchoServerStruct {
  implicit val rw: ReaderWriter[AdminEchoServerStruct] = ccRW
}