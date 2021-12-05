package com.outr.arango.core

sealed trait ConsolidationPolicy

object ConsolidationPolicy {
  case class BytesAccum(threshold: Double = 0.85) extends ConsolidationPolicy
  case class Tier(segmentThreshold: Long = 300) extends ConsolidationPolicy
}