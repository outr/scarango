package com.outr.arango.core

import com.outr.arango.ArangoError

case class DeleteResults(results: List[Either[ArangoError, DeleteResult]]) {
  lazy val documents: List[DeleteResult] = results.collect {
    case Right(dr) => dr
  }
  lazy val errors: List[ArangoError] = results.collect {
    case Left(e) => e
  }
}