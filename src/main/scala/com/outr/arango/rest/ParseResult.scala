package com.outr.arango.rest

case class ParseResult(error: Boolean,
                       code: Int,
                       parsed: Boolean,
                       collections: List[String],
                       bindVars: List[String],
                       ast: List[ParsedAST])