package com.outr.arango.managed

import gnieh.diffson.Pointer
import io.circe._
import gnieh.diffson.circe._

object Diff {
  def diff(v1: Json, v2: Json): Json = makeSimpleDiff(v1, JsonDiff.simpleDiff(v1, v2, remember = false))

  private def makePath(fields: Map[String, Json], path: Seq[String], value: Json): Map[String, Json] =
    path match {
      case Seq() => Map.empty
      case Seq(last) => fields.updated(last, value)
      case Seq(h, t @ _*) =>
        fields.get(h) match {
          case Some(obj) if obj.isObject => fields.updated(h, Json.fromJsonObject(JsonObject.fromMap(makePath(obj.asObject.get.toMap, t, value))))
          case Some(other) => fields.updated(h, other)
          case None => fields.updated(h, Json.fromJsonObject(JsonObject.fromMap(makePath(Map.empty, t, value))))
        }
    }

  private def makeSimpleDiff(originalValue: Json, patch: JsonPatch): Json = {
    val fields = patch.ops.foldLeft(Map.empty[String, Json]) {
      case (acc, Add(Pointer(path @ _*), value)) => makePath(acc, path, value)
      case (acc, Replace(Pointer(path @ _*), value, _)) => makePath(acc, path, value)
      case (acc, Remove(Pointer(path @ _*), _)) => makePath(acc, path, Json.Null)
    }
    Json.fromJsonObject(JsonObject.fromMap(fields))
  }
}