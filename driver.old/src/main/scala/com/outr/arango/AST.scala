package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class AST(`type`: String,
               name: Option[String],
               id: Option[Int],
               subNodes: Option[List[AST]])

object AST {
  implicit val rw: ReaderWriter[AST] = ccRW
}