package com.outr.arango

class ArangoException(val error: ArangoError, val message: String) extends RuntimeException(s"$message ($error)")
