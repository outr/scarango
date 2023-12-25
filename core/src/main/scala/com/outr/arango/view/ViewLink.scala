package com.outr.arango.view

import com.outr.arango.collection.Collection
import com.outr.arango.{Analyzer, Field}

case class ViewLink(collection: Collection,
                    analyzers: List[Analyzer] = List(Analyzer.Identity),
                    fields: List[(Field[_], List[Analyzer])] = Nil,
                    includeAllFields: Boolean = false,
                    trackListPositions: Boolean = false,
                    storeValues: Boolean = false,
                    inBackground: Boolean = false)
