package com.outr.arango

sealed trait Index

object Index {
  case class Primary(fields: List[String]) extends Index
  case class Persistent(fields: List[String],
                        sparse: Boolean = false,
                        unique: Boolean = false,
                        estimates: Boolean = true) extends Index

  case class Geo(fields: List[String], geoJson: Boolean = true) extends Index

  case class TTL(fields: List[String], expireAfterSeconds: Int = -1) extends Index

  case class Inverted(parallelism: Int = 2,
                      fields: List[InvertedIndexField],
                      analyzer: Analyzer = Analyzer.Identity,
                      features: Set[AnalyzerFeature] = Set.empty,
                      includeAllFields: Boolean = false,
                      trackListPositions: Boolean = false,
                      searchField: Boolean = false,
                      cache: Boolean = false,
                      primaryKeyCache: Boolean = false) extends Index
}