package com.outr.arango.api.model

import io.circe.Json

/**
  * PostAPIAqlfunctionRc400
  *
  * @param error boolean flag to indicate whether an error occurred ({@literal *}true{@literal *} in this case)
  * @param code the HTTP status code
  * @param errorMessage a descriptive error message
  * @param errorNum the server error number
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class PostAPIAqlfunctionRc400(error: Boolean,
                                   code: Option[Long] = None,
                                   errorMessage: Option[String] = None,
                                   errorNum: Option[Long] = None)