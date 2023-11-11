package com.outr.arango.geo

import com.outr.arango.geo.GeoJSON._
import fabric._
import fabric.define.DefType
import fabric.rw._

sealed trait GeoJSON

object GeoJSON {
  private[geo] def addType[T <: GeoJSON](name: String)(t: T, json: Json): Json = json.merge(obj(
    "type" -> name
  ))
  private[geo] def pointArray(p: GeoPoint): Json = arr(p.longitude, p.latitude)
  private[geo] def pointFromCoords(json: Json): GeoPoint = {
    val arr = json.asArr
    GeoPoint(
      latitude = arr.value(1).asDouble,
      longitude = arr.value(0).asDouble
    )
  }
  private[geo] def pointsFromCoords(json: Json): List[GeoPoint] = json.asArr.value.toList.map(pointFromCoords)
  private[geo] def multiPointsFromCoords(json: Json): List[List[GeoPoint]] = json.asArr.value.toList.map(pointsFromCoords)
  private[geo] def createRW[T <: GeoJSON](toCoordinates: T => Json, fromCoordinates: Json => T): RW[T] = RW.from(
    r = t => obj(
      "coordinates" -> toCoordinates(t)
    ),
    w = j => fromCoordinates(j("coordinates")),
    d = DefType.Json
  )

  implicit lazy val rw: RW[GeoJSON] = RW.poly[GeoJSON](getType = _.getClass.getSimpleName.replace("$", ""))(
    "GeoPoint" -> GeoPoint.rw,
    "GeoMultiPoint" -> GeoMultiPoint.rw,
    "GeoLineString" -> GeoLineString.rw,
    "GeoMultiLineString" -> GeoMultiLineString.rw,
    "GeoPolygon" -> GeoPolygon.rw,
    "GeoMultiPolygon" -> GeoMultiPolygon.rw
  )
}

case class GeoPoint(latitude: Double, longitude: Double) extends GeoJSON

object GeoPoint {
  implicit val rw: RW[GeoPoint] = createRW[GeoPoint](
    point => pointArray(point),
    pointFromCoords
  ).withPostRead(addType("GeoPoint"))
}

case class GeoMultiPoint(points: List[GeoPoint]) extends GeoJSON

object GeoMultiPoint {
  implicit val rw: RW[GeoMultiPoint] = createRW[GeoMultiPoint](
    mp => mp.points.map(pointArray).json,
    json => GeoMultiPoint(pointsFromCoords(json))
  ).withPostRead(addType("GeoMultiPoint"))
}

case class GeoLineString(points: List[GeoPoint]) extends GeoJSON

object GeoLineString {
  implicit val rw: RW[GeoLineString] = createRW[GeoLineString](
    ls => ls.points.map(pointArray).json,
    json => GeoLineString(pointsFromCoords(json))
  ).withPostRead(addType("GeoLineString"))
}

case class GeoMultiLineString(lines: List[List[GeoPoint]]) extends GeoJSON

object GeoMultiLineString {
  implicit val rw: RW[GeoMultiLineString] = createRW[GeoMultiLineString](
    mls => mls.lines.map(_.map(pointArray)).json,
    json => GeoMultiLineString(multiPointsFromCoords(json))
  ).withPostRead(addType("GeoMultiLineString"))
}

case class GeoPolygon(points: List[GeoPoint]) extends GeoJSON

object GeoPolygon {
  implicit val rw: RW[GeoPolygon] = createRW[GeoPolygon](
    p => p.points.map(pointArray).json,
    json => GeoPolygon(pointsFromCoords(json))
  ).withPostRead(addType("GeoPolygon"))
}

case class GeoMultiPolygon(polygons: List[List[GeoPoint]]) extends GeoJSON

object GeoMultiPolygon {
  implicit val rw: RW[GeoMultiPolygon] = createRW[GeoMultiPolygon](
    mp => mp.polygons.map(_.map(pointArray)).json,
    json => GeoMultiPolygon(multiPointsFromCoords(json))
  ).withPostRead(addType("GeoMultiPolygon"))
}