package com.outr.arango.core

sealed trait OverwriteMode

object OverwriteMode {
  case object None extends OverwriteMode
  case object Ignore extends OverwriteMode
  case object Replace extends OverwriteMode
  case object Update extends OverwriteMode
  case object UpdateMerge extends OverwriteMode
  case object Conflict extends OverwriteMode
}