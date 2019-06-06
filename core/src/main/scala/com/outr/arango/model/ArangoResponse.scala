package com.outr.arango.model

case class ArangoResponse[R](error: Boolean, code: Int, result: R)