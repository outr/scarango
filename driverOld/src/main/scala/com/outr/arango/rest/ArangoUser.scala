package com.outr.arango.rest

import io.circe.Json

case class ArangoUser(username: String, passwd: String = "", active: Boolean = true, extra: Json = Json.obj())