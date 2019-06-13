package com.outr.arango.model

case class ArangoResponseException(message: String, code: Int) extends RuntimeException(message)
