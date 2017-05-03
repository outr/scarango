package com.outr.arango

class ArangoException(val error: ArangoError, val message: String, val request: Any) extends RuntimeException(s"$message ($error) for $request.")
