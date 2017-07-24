package com.outr.arango.rest

case class CreateDatabaseRequest( name: String, users : List[ArangoUser] = List())
case class ArangoUser(username: String, passwd: Option[String], active: Option[Boolean] = Some(true))
case class CreateDatabaseResponse( result: Boolean, error: Boolean, code: Int)


