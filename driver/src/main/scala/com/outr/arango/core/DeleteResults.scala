package com.outr.arango.core

import com.outr.arango.ArangoError

case class DeleteResults[T](results: List[Either[ArangoError, DeleteResult[T]]]) {
  lazy val documents: List[DeleteResult[T]] = results.collect {
    case Right(dr) => dr
  }
  lazy val errors: List[ArangoError] = results.collect {
    case Left(e) => e
  }
}