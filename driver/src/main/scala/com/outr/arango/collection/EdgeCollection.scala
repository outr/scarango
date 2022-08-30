package com.outr.arango.collection

import com.outr.arango.core.ArangoDBCollection
import com.outr.arango.{CollectionType, Edge, EdgeModel, Graph}

class EdgeCollection[E <: Edge[E, From, To], From, To](graph: Graph,
                                                       arangoDBCollection: ArangoDBCollection,
                                                       edgeModel: EdgeModel[E, From, To],
                                                       managed: Boolean)
  extends DocumentCollection[E](graph, arangoDBCollection, edgeModel, CollectionType.Edge, managed)