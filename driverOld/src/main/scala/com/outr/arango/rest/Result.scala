package com.outr.arango.rest

case class Result[T](result: T, error: Boolean, code: Int)