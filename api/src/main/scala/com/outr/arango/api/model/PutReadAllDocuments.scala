package com.outr.arango.api.model

case class PutReadAllDocuments(collection: String,
                               `type`: Option[String] = None)

