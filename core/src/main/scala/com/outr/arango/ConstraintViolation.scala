package com.outr.arango

case class ConstraintViolation(index: String, `type`: String, fields: Set[String], key: String)