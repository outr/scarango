package com.outr.arango.core

sealed trait LoadBalancingStrategy

object LoadBalancingStrategy {
  case object None extends LoadBalancingStrategy
  case object RoundRobin extends LoadBalancingStrategy
  case object OneRandom extends LoadBalancingStrategy
}