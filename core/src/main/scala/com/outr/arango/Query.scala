package com.outr.arango

import io.circe.Json

case class Query(value: String, args: Map[String, Value]) {
  def fixed(): Query = if (args.valuesIterator.map(_.json).contains(Json.Null)) {
    var updated = value
    val filteredArgs = args.filter {
      case (k, v) => if (v.json == Json.Null) {
        updated = updated.replaceAllLiterally(s"@$k", "null")
        false
      } else {
        true
      }
    }
    copy(updated, filteredArgs)
  } else {
    this
  }

  def +(that: Query): Query = Query.merge(List(this, that))

  def bindVars: Json = Json.obj(args.toList.map {
    case (key, v) => {
      val argValue: Json = v.json
      key -> argValue
    }
  }: _*)
}

object Query {
  def merge(queries: List[Query], concat: String = "\n"): Query = {
    val value = queries.map(_.value).mkString(concat)
    val args = queries.flatMap(_.args)
    Query(value, args.toMap)
  }
}