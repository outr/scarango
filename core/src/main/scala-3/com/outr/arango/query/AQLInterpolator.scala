package com.outr.arango.query

class AQLInterpolator(val sc: StringContext) extends AnyVal {
  def aql(args: QueryPart*): Query = {
    val strings = sc.parts.iterator
    val expressions = args.iterator
    var parts = List.empty[QueryPart]
    while (strings.hasNext || expressions.hasNext) {
      if (strings.hasNext) {
        parts = QueryPart.Static(strings.next()) :: parts
      }
      if (expressions.hasNext) {
        val part = expressions.next()
        parts = part :: parts
      }
    }
    Query(parts.reverse)
  }
}