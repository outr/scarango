package com.outr.arango.query

import com.outr.arango.Query

class Filter(left: Query, condition: String, right: Query) {
  def &&(filter: Filter): Filter = {
    new Filter(build(), "&&", filter.build())
  }
  def ||(filter: Filter): Filter = {
    new Filter(build(), "||", filter.build())
  }

  def build(): Query = Query(s"${left.value} $condition ${right.value}", left.args ++ right.args)
}
