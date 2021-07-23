package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class DocumentInsert(_id: Option[Id[DocumentInsert]],
                          _oldRev: String = "",
                          `new`: Option[fabric.Value],
                          old: Option[fabric.Value])

object DocumentInsert {
  implicit val rw: ReaderWriter[DocumentInsert] = ccRW
}