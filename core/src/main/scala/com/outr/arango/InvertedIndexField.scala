package com.outr.arango

case class InvertedIndexField(name: String,
                              analyzer: Analyzer = Analyzer.Identity,
                              includeAllFields: Boolean = false,
                              searchField: Boolean = false,
                              trackListPositions: Boolean = false,
                              cache: Boolean = false,
                              features: Set[AnalyzerFeature] = Set.empty)
