package com.outr.arango

import scala.language.experimental.macros

trait Modifiable {
  def modified: Long
}

object Modifiable {
  def updateIfModifiable[T](value: T): T = macro CoreMacros.updateIfModifiable[T]
}