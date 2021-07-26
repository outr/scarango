package com.outr.arango

case class Query(parts: List[QueryPart]) {
  lazy val (variables: Map[String, fabric.Value], reverseLookup: Map[fabric.Value, String]) = {
    var counter = 0
    var map = Map.empty[String, fabric.Value]
    var reverseMap = Map.empty[fabric.Value, String]
    parts.foreach {
      case QueryPart.Variable(v) =>
        val id = reverseMap.get(v) match {
          case Some(idValue) => idValue
          case None =>
            val idValue = s"arg$counter"
            counter += 1
            idValue
        }
        map += id -> v
        reverseMap += v -> id
      case QueryPart.NamedVariable(name, v) => map.get(name) match {
        case Some(value) if v != value => throw new RuntimeException(s"Duplicate named variable with different values: $name with $v and $value")
        case Some(_) => // Already added
        case None =>
          val id = name
          map += id -> v
          reverseMap += v -> id
      }
      case _ => // Ignore static
    }
    map -> reverseMap
  }

  lazy val string: String = parts.map {
    case QueryPart.Static(v) => v
    case QueryPart.Variable(v) => s"@${reverseLookup(v)}"
    case QueryPart.NamedVariable(name, _) => s"@$name"
  }.mkString

  def +(that: Query): Query = Query.merge(List(this, that))

  override def toString: String = s"$string (${variables.map(t => s"${t._1}: ${t._2}")})"
}

object Query {
  def apply(query: String): Query = Query(List(QueryPart.Static(query)))

  def apply(parts: QueryPart*): Query = Query(parts.toList)

  /**
    * Merges queries and renames overlapping argument names
    *
    * @param queries the list of queries to merge
    * @param separator the separator string between each query
    * @return merge quest
    */
  def merge(queries: List[Query], separator: String = "\n"): Query = {
    val parts = queries.map(_.parts).foldLeft(List.empty[QueryPart])((merged, current) => {
      if (merged.isEmpty) {
        current
      } else {
        merged ::: List(QueryPart.Static(separator)) ::: current
      }
    })
    Query(parts)
  }
}