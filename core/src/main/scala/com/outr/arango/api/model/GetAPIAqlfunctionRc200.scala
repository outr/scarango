package com.outr.arango.api.model

import io.circe.Json

/**
  * GetAPIAqlfunctionRc200
  *
  * @param error boolean flag to indicate whether an error occurred ({@literal *}false{@literal *} in this case)
  * @param code the HTTP status code
  * @param result *** No description ***
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class GetAPIAqlfunctionRc200(error: Boolean,
                                  code: Option[Long] = None,
                                  result: Option[AqlUserfunctionStruct] = None)