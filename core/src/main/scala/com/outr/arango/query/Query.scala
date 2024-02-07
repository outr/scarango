package com.outr.arango.query

import fabric.Json
import fabric.io.JsonFormatter

case class Query(parts: List[QueryPart], options: QueryOptions) extends QueryPart.Support with QueryOptionsSupport[Query] {
  lazy val (variables: Map[String, fabric.Json], reverseLookup: Map[String, String]) = {
    var argCounter = 0
    var map = Map.empty[String, fabric.Json]
    var reverseMap = Map.empty[String, String]

    def parsePart(part: QueryPart): Unit = part match {
      case QueryPart.Ref(_) => // Ignore ref
      case QueryPart.Container(parts) => parts.foreach(parsePart)
      case QueryPart.Static(_) => // Ignore static
      case QueryPart.Variable(v) =>
        val id = reverseMap.get(JsonFormatter.Compact(v)) match {
          case Some(idValue) => idValue
          case None =>
            val idValue = s"arg$argCounter"
            argCounter += 1
            idValue
        }
        map += id -> v
        reverseMap += JsonFormatter.Compact(v) -> id
      case QueryPart.NamedVariable(name, v) => map.get(name) match {
        case Some(value) if v != value => throw new RuntimeException(s"Duplicate named variable with different values: $name with $v and $value")
        case Some(_) => // Already added
        case None =>
          val id = name
          map += id -> v
          reverseMap += JsonFormatter.Compact(v) -> id
      }
      case support: QueryPart.Support => parsePart(support.toQueryPart)
    }

    parts.foreach(parsePart)
    map -> reverseMap
  }

  def byName(key: String): fabric.Json = variables.getOrElse(key, throw new RuntimeException(s"Unable to find $key in ${variables.keys.mkString(", ")}"))
  def byValue(value: fabric.Json): String = reverseLookup.getOrElse(
    JsonFormatter.Compact(value),
    throw new RuntimeException(s"Unable to find $value in ${reverseLookup.mkString(", ")}")
  )

  lazy val string: String = asString(new RefManager)

  override def withOptions(f: QueryOptions => QueryOptions): Query = copy(options = f(options))

  private def asString(refManager: RefManager): String = {
    def part2String(part: QueryPart): String = part match {
      case QueryPart.Ref(ref) => refManager.nameFor(ref)
      case QueryPart.Container(parts) => parts.map(part2String).mkString
      case QueryPart.Static(v) => v
      case QueryPart.Variable(v) => s"@${byValue(v)}"
      case QueryPart.NamedVariable(name, _) => s"@$name"
      case support: QueryPart.Support => part2String(support.toQueryPart)
    }

    parts.map(part2String).mkString
  }

  lazy val compressed: String = string.replaceAll("\\s+", " ")

  def +(that: Query): Query = Query.merge(List(this, that))

  def withQuery(that: Query): Query = this + that

  def withPrefixParts(parts: QueryPart*): Query = copy(parts.toList ::: this.parts)
  def withParts(parts: QueryPart*): Query = copy(this.parts ::: parts.toList)
  def static(value: String): Query = withParts(QueryPart.Static(value))
  def variable(value: Json): Query = withParts(QueryPart.Variable(value))
  def namedVariable(name: String, value: Json): Query = withParts(QueryPart.NamedVariable(name, value))

  override def toQueryPart: QueryPart = QueryPart.Container(parts)

  override def toString: String = s"$string (${variables.map(t => s"${t._1}: ${t._2}")})"

  def normalize: Query = Query(parts.flatMap {
    case QueryPart.Static(value) => value.split('\n').toList.filter(_.trim.nonEmpty).map(s => QueryPart.Static(s"\n$s"))
    case part => List(part)
  }, options)

  override def equals(obj: Any): Boolean = obj match {
    case that: Query => this.compressed == that.compressed && this.variables == that.variables
    case _ => false
  }
}

object Query extends Query(Nil, QueryOptions()) {
  def apply(query: String): Query = Query(List(QueryPart.Static(query)), options)
  def apply(parts: List[QueryPart]): Query = Query(parts, options)

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
    Query(parts, QueryOptions.merge(queries.map(_.options)))
  }
}