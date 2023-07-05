package com.outr.arango.core

sealed trait ComputeOn

object ComputeOn {
  case object Insert extends ComputeOn

  case object Update extends ComputeOn

  case object Replace extends ComputeOn
}