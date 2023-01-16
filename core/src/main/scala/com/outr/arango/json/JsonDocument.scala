package com.outr.arango.json

import com.outr.arango.{Document, Id}
import fabric._

case class JsonDocument(json: Json, _id: Id[JsonDocument]) extends Document[JsonDocument]