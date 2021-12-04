package com.outr.arango

import fabric.Value
import fabric.rw._

case class StoreValue(_key: String, value: Value)

object StoreValue {
  implicit val rw: ReaderWriter[StoreValue] = ccRW
}