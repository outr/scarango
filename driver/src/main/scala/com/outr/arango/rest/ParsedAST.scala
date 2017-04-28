package com.outr.arango.rest

case class ParsedAST(`type`: String,
                     name: Option[String],
                     id: Option[Int],
                     subNodes: Option[List[ParsedAST]])