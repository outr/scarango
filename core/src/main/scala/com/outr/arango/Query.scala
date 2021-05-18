package com.outr.arango

import fabric.{Null, obj}
import fabric.parse.Json

case class Query(value: String, args: Map[String, Value], fixed: Boolean = false) {
  def fix(): Query = if (fixed) {
    this
  } else {
    var updatedValue = value
    val updatedArgs = args.flatMap {
      case (k, v) if v.static => {
        val key = if (v.excludeAt) {
          k
        } else {
          s"@$k"
        }
        updatedValue = updatedValue.replace(key, Json.format(v.json))
        None
      }
      case (k, v) if v.json == Null => {
        val key = if (v.excludeAt) {
          k
        } else {
          s"@$k"
        }
        updatedValue = updatedValue.replace(key, "null")
        None
      }
      case (k, v) => Some((k, v))
    }
    Query(updatedValue, updatedArgs, fixed = true)
  }

  def +(that: Query): Query = Query.merge(List(this, that))

  def bindVars: fabric.Value = obj(args.toList.map {
    case (key, v) => {
      val argValue: fabric.Value = v.json
      key -> argValue
    }
  }: _*)

  override def toString: String = s"[$value] (${args.map(t => s"${t._1}: ${t._2}").mkString(", ")})"
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

      @scala.annotation.tailrec
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
            value = query.value.replace(s"@$key", s"@$newKey"),
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