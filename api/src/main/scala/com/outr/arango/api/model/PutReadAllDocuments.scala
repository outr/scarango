package com.outr.arango.api.model

import io.circe.Json


case class PutReadAllDocuments(collection: String,
                               `type`: Option[String] = None)