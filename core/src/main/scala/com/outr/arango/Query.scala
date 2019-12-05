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
  private val ExtractNumeric = """(.+)(\d+)""".r

  /**
    * Merges queries and renames overlapping argument names
    *
    * @param queries the list of queries to merge
    * @param separator the separator string between each query
    * @return merge quest
    */
  def merge(queries: List[Query], separator: String = "\n"): Query = {
    var usedKeys = Set.empty[String]
    val updatedQueries: List[Query] = queries.map { q =>
      var query = q
      val localKeys = query.args.keys.toSet

      def nextKey(key: String): String = key match {
        case ExtractNumeric(prefix, n) => {
          val newKey = s"$prefix${n.toInt + 1}"
          if (!usedKeys.contains(newKey) && !localKeys.contains(newKey)) {
            newKey
          } else {
            nextKey(newKey)
          }
        }
        case _ => nextKey(s"${key}1")
      }

      query.args.keys.foreach {
        case key if usedKeys.contains(key) => {
          val newKey = nextKey(key)
          usedKeys += newKey
          query = query.copy(
            value = query.value.replaceAllLiterally(s"@$key", s"@$newKey"),
            args = query.args.map {
              case (k, v) if k == key => newKey -> v
              case (k, v) => k -> v
            }
          )
        }
        case key => usedKeys += key
      }
      query
    }
    val value = updatedQueries.map(_.value).mkString(separator)
    val args = updatedQueries.flatMap(_.args).toMap
    Query(value, args)
  }
}