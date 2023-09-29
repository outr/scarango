package com.outr.arango.upsert

import fabric.rw.RW

case class UpsertResult[D](original: Option[D], newValue: D)

object UpsertResult {
  implicit def rw[D: RW]: RW[UpsertResult[D]] = RW.gen
}