package com.outr.arango.core

import com.outr.arango.ArangoError

case class CreateResults(results: List[Either[ArangoError, CreateResult]]) {
  lazy val documents: List[CreateResult] = results.collect {
    case Right(cr) => cr
  }
  lazy val errors: List[ArangoError] = results.collect {
    case Left(e) => e
  }
}