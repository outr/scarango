package com.outr.arango.core

case class ASTNode(`type`: String, subNodes: List[ASTNode], name: String, id: Long, value: AnyRef)