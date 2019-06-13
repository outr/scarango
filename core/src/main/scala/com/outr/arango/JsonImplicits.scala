package com.outr.arango

import io.circe.Json

object JsonImplicits {
  implicit class JsonExtras(val json: Json) extends AnyVal {
    def \(key: String): Option[Json] = json.asObject.flatMap(_(key))
  }

  implicit class JsonOptionExtras(val jsonOption: Option[Json]) extends AnyVal {
    def \(key: String): Option[Json] = jsonOption.flatMap(_.asObject.flatMap(_(key)))
  }
}