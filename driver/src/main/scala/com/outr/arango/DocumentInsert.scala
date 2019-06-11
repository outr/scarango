package com.outr.arango

import io.circe.Json

case class DocumentInsert(_id: String = "",
                          _key: String = "",
                          _rev: String = "",
                          _oldRev: String = "",
                          `new`: Option[Json],
                          old: Option[Json])
