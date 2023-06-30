package com.outr.arango

import com.arangodb.ArangoDBException

case class ArangoException(cause: ArangoDBException) extends RuntimeException(cause.getMessage, cause) {
  lazy val error: ArangoError = ArangoError(
    code = cause.getResponseCode,
    num = cause.getErrorNum,
    message = cause.getErrorMessage,
    exception = cause.getException
  )

  lazy val constraintViolation: Option[ConstraintViolation] = if (error.errorCode == ArangoCode.ArangoUniqueConstraintViolated) {
    getMessage match {
      case ArangoException.ConstraintViolationRegex(index, tpe, field, key) =>
        Some(ConstraintViolation(index, tpe, field, key))
      case s => throw new RuntimeException(s"Failed to parse constraint violation: $s")
    }
  } else {
    None
  }
}

case class ConstraintViolation(index: String, `type`: String, field: String, key: String)

object ArangoException {
  private val ConstraintViolationRegex = """.+unique constraint violated - in index (\S+) of type (.+) over '(.+?)'; conflicting key: (\S+)""".r
}