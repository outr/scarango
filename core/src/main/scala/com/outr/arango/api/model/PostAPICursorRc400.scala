package com.outr.arango.api.model

import io.circe.Json

/**
  * PostAPICursorRc400
  *
  * @param error boolean flag to indicate that an error occurred ({@literal *}true{@literal *} in this case)
  * @param code the HTTP status code
  * @param errorMessage a descriptive error message
  *        
  *        If the query specification is complete, the server will process the query. If an
  *        error occurs during query processing, the server will respond with {@literal *}HTTP 400{@literal *}.
  *        Again, the body of the response will contain details about the error.
  *        
  *        A [list of query errors can be found here](../../Manual/Appendix/ErrorCodes.html).
  * @param errorNum the server error number
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class PostAPICursorRc400(error: Boolean,
                              code: Option[Long] = None,
                              errorMessage: Option[String] = None,
                              errorNum: Option[Long] = None)