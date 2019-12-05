package com.outr.arango.api.model

case class CreateDatabaseOptions(sharding: Option[String], replicationFactor: Option[String], writeConcern: Option[String])