package com.outr.arango.core

case class AQLParseResult(collections: List[String], bindVars: List[String], ast: List[ASTNode])