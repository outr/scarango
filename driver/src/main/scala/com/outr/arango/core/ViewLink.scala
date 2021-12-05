package com.outr.arango.core

import com.outr.arango.{Analyzer, Collection, Field}

case class ViewLink(collection: Collection,
                    analyzers: List[Analyzer] = List(Analyzer.Identity),
                    fields: List[(Field[_], List[Analyzer])] = Nil,
                    includeAllFields: Boolean = false,
                    trackListPositions: Boolean = false,
                    storeValues: Boolean = false,
                    inBackground: Boolean = false)