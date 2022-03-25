package com.outr.arango.query

import fabric.Value

case class Query(parts: List[QueryPart]) extends QueryPart.Support {
  lazy val (variables: Map[String, fabric.Value], reverseLookup: Map[fabric.Value, String]) = {
    var counter = 0
    var map = Map.empty[String, fabric.Value]
    var reverseMap = Map.empty[fabric.Value, String]

    def parsePart(part: QueryPart): Unit = part match {
      case QueryPart.Container(parts) => parts.foreach(parsePart)
      case QueryPart.Static(_) => // Ignore static
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
      case support: QueryPart.Support => parsePart(support.toQueryPart)
    }

    parts.foreach(parsePart)
    map -> reverseMap
  }

  lazy val string: String = parts.map(part2String).mkString

  def compressed: String = string.replaceAll("\\s+", " ")

  private def part2String(part: QueryPart): String = part match {
    case QueryPart.Container(parts) => parts.map(part2String).mkString
    case QueryPart.Static(v) => v
    case QueryPart.Variable(v) => s"@${reverseLookup(v)}"
    case QueryPart.NamedVariable(name, _) => s"@$name"
    case support: QueryPart.Support => part2String(support.toQueryPart)
  }

  def +(that: Query): Query = Query.merge(List(this, that))

  def withQuery(that: Query): Query = this + that

  def withParts(parts: QueryPart*): Query = copy(this.parts ::: parts.toList)
  def static(value: String): Query = withParts(QueryPart.Static(value))
  def variable(value: Value): Query = withParts(QueryPart.Variable(value))
  def namedVariable(name: String, value: Value): Query = withParts(QueryPart.NamedVariable(name, value))

  override def toQueryPart: QueryPart = QueryPart.Container(parts)

  override def toString: String = s"$string (${variables.map(t => s"${t._1}: ${t._2}")})"

  override def equals(obj: Any): Boolean = obj match {
    case that: Query => this.compressed == that.compressed && this.variables == that.variables
    case _ => false
  }
}

object Query extends Query(Nil) {
  def apply(query: String): Query = Query(List(QueryPart.Static(query)))

//  def apply(parts: QueryPart*): Query = Query(parts.toList)

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