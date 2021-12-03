package com.outr.arango

import fabric.Value
import fabric.rw._

case class StoreValue(key: String, value: Value)

object StoreValue {
  implicit val rw: ReaderWriter[StoreValue] = ccRW
}