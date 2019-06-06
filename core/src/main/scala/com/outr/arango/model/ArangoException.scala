package com.outr.arango.model

case class ArangoException(message: String, code: Int) extends RuntimeException(message)
