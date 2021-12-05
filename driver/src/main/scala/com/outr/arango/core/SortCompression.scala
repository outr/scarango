package com.outr.arango.core

sealed trait SortCompression

object SortCompression {
  case object LZ4 extends SortCompression
  case object None extends SortCompression
}