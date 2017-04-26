package com.outr.arango.managed

import com.outr.arango.DocumentOption

abstract class PolymorphicCollection[T <: DocumentOption](graph: Graph, name: String)
  extends Collection[T](graph, name) {

}
