package com.outr.arango.dsl

import com.outr.arango.Value

trait QueryPart {
  def aql: String
  def args: List[Value] = List.empty
}