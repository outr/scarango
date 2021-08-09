package com.outr.arango.core

import com.arangodb.async.ArangoDBAsync

class ArangoDBServer(connection: ArangoDBAsync) {
  lazy val db: ArangoDB = new ArangoDB(connection.db())

  def db(name: String): ArangoDB = new ArangoDB(connection.db(name))
}

object ArangoDBServer {
  def apply(connection: ArangoDBAsync): ArangoDBServer = new ArangoDBServer(connection)

  // TODO: add configuration options
  def apply(password: Option[String] = None): ArangoDBServer = apply(new ArangoDBAsync.Builder()
    .password(password.orNull)
    .build())
}