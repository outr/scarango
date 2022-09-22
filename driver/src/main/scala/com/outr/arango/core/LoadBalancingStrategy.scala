package com.outr.arango.core

import fabric.rw._

sealed trait LoadBalancingStrategy

object LoadBalancingStrategy {
  implicit val rw: RW[LoadBalancingStrategy] = RW.enumeration(
    list = List(None, RoundRobin, OneRandom),
    asString = _.getClass.getSimpleName.toLowerCase.replace("$", "")
  )

  case object None extends LoadBalancingStrategy
  case object RoundRobin extends LoadBalancingStrategy
  case object OneRandom extends LoadBalancingStrategy
}