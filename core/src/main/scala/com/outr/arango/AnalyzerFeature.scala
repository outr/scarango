package com.outr.arango

sealed trait AnalyzerFeature

object AnalyzerFeature {
  case object Frequency extends AnalyzerFeature
  case object Norm extends AnalyzerFeature
  case object Position extends AnalyzerFeature
  case object Offset extends AnalyzerFeature
}