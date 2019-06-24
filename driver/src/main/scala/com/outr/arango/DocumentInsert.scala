package com.outr.arango

import io.circe.Json

case class DocumentInsert(_id: Option[Id[DocumentInsert]],
                          _oldRev: String = "",
                          `new`: Option[Json],
                          old: Option[Json])
