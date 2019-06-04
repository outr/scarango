package com.outr.arango.api.model

import io.circe.Json

/**
  * UserHandlingCreate
  *
  * @param user The name of the user as a string. This is mandatory.
  * @param active An optional flag that specifies whether the user is active.  If not
  *        specified, this will default to true
  * @param extra An optional JSON object with arbitrary extra data about the user.
  * @param passwd The user password as a string. If no password is specified, the empty string
  *        will be used. If you pass the special value {@literal *}ARANGODB_DEFAULT_ROOT_PASSWORD{@literal *},
  *        then the password will be set the value stored in the environment variable
  *        `ARANGODB_DEFAULT_ROOT_PASSWORD`. This can be used to pass an instance
  *        variable into ArangoDB. For example, the instance identifier from Amazon.
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class UserHandlingCreate(user: String,
                              active: Option[Boolean] = None,
                              extra: Option[Json] = None,
                              passwd: Option[String] = None)