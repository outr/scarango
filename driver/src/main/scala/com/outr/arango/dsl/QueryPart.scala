package com.outr.arango.dsl

import com.outr.arango.QueryArg

trait QueryPart {
  def aql: String
  def args: List[QueryArg] = List.empty
}
