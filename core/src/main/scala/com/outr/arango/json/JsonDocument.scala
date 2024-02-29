package com.outr.arango.json

import com.outr.arango.{Document, Id}
import fabric._

/**
  * JsonDocument is a convenience class that extends Document and JsonWrapper to provide a simple way to work with
  * ArangoDB documents that are stored as JSON.
  */
trait JsonDocument extends Document[JsonDocument] with JsonWrapper