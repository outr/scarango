package com.outr.arango.rest

case class CreateDatabaseRequest(name: String, users: List[ArangoUser])