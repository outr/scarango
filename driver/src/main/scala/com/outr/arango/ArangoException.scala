package com.outr.arango

import io.youi.http.Method
import io.youi.net.URL

class ArangoException(val error: ArangoError,
                      val message: String,
                      val request: Any,
                      val method: Method,
                      val url: URL) extends RuntimeException(s"$message ($error) for $request @ $url (${method.value}).")
