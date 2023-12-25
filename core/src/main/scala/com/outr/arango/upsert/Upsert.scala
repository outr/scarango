package com.outr.arango.upsert

import com.outr.arango.Document

sealed trait Upsert[D <: Document[D]]

object Upsert {
  case class Update[D <: Document[D]](value: String) extends Upsert[D]
  case class Replace[D <: Document[D]](replacement: D) extends Upsert[D]
}