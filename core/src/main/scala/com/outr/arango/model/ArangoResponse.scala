package com.outr.arango.model

case class ArangoResponse[R](error: Boolean,
                             errorMessage: Option[String],
                             errorNum: Int = -1,
                             code: Int,
                             result: R)