package com.outr.arango.rest

import com.outr.arango.ArangoCode

case class ParseResult(error: Boolean,
                       errorMessage: Option[String],
                       errorNum: Option[Int],
                       code: Int,
                       parsed: Boolean,
                       collections: List[String],
                       bindVars: List[String],
                       ast: List[ParsedAST]) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum.get)
}