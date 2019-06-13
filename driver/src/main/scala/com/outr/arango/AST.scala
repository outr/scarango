package com.outr.arango

case class AST(`type`: String,
               name: Option[String],
               id: Option[Int],
               subNodes: Option[List[AST]])
