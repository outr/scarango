package com.outr.arango.core

case class NotFoundException(key: String) extends RuntimeException(s"$key was not found")