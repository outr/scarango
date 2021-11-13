package com.outr.arango.core

import com.outr.arango.ArangoError

case class CreateResults[T](results: List[Either[ArangoError, CreateResult[T]]]) {
  lazy val documents: List[CreateResult[T]] = results.collect {
    case Right(cr) => cr
  }
  lazy val errors: List[ArangoError] = results.collect {
    case Left(e) => e
  }
}