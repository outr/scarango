package com.outr.arango.core

sealed trait Level

object Level {
  case object None extends Level
  case object New extends Level
  case object Moderate extends Level
  case object Strict extends Level
}