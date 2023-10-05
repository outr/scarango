package com.outr.arango.geo

import fabric._
import fabric.define.DefType
import fabric.rw._

sealed trait GeoJSON

object GeoJSON {
  private def pointArray(p: Point): Json = arr(p.longitude, p.latitude)
  private def pointFromCoords(json: Json): Point = {
    val arr = json.asArr
    Point(
      latitude = arr.value(1).asDouble,
      longitude = arr.value(0).asDouble
    )
  }
  private def pointsFromCoords(json: Json): List[Point] = json.asArr.value.toList.map(pointFromCoords)
  private def multiPointsFromCoords(json: Json): List[List[Point]] = json.asArr.value.toList.map(pointsFromCoords)
  private def createRW[T <: GeoJSON](toCoordinates: T => Json, fromCoordinates: Json => T): RW[T] = RW.from(
    r = t => obj(
      "coordinates" -> toCoordinates(t)
    ),
    w = j => fromCoordinates(j("coordinates")),
    d = DefType.Json
  )
  private lazy val pointRW: RW[Point] = createRW[Point](
    point => pointArray(point),
    pointFromCoords
  )
  private lazy val multiPointRW: RW[MultiPoint] = createRW[MultiPoint](
    mp => mp.points.map(pointArray).json,
    json => MultiPoint(pointsFromCoords(json))
  )
  private lazy val lineStringRW: RW[LineString] = createRW[LineString](
    ls => ls.points.map(pointArray).json,
    json => LineString(pointsFromCoords(json))
  )
  private lazy val multiLineStringRW: RW[MultiLineString] = createRW[MultiLineString](
    mls => mls.lines.map(_.map(pointArray)).json,
    json => MultiLineString(multiPointsFromCoords(json))
  )
  private lazy val polygonRW: RW[Polygon] = createRW[Polygon](
    p => p.points.map(pointArray).json,
    json => Polygon(pointsFromCoords(json))
  )
  private lazy val multiPolygonRW: RW[MultiPolygon] = createRW[MultiPolygon](
    mp => mp.polygons.map(_.map(pointArray)).json,
    json => MultiPolygon(multiPointsFromCoords(json))
  )

  implicit lazy val rw: RW[GeoJSON] = RW.poly[GeoJSON](getType = _.getClass.getSimpleName.replace("$", ""))(
    "Point" -> pointRW,
    "MultiPoint" -> multiPointRW,
    "LineString" -> lineStringRW,
    "MultiLineString" -> multiLineStringRW,
    "Polygon" -> polygonRW,
    "MultiPolygon" -> multiPolygonRW
  )

  case class Point(latitude: Double, longitude: Double) extends GeoJSON
  case class MultiPoint(points: List[Point]) extends GeoJSON
  case class LineString(points: List[Point]) extends GeoJSON
  case class MultiLineString(lines: List[List[Point]]) extends GeoJSON
  case class Polygon(points: List[Point]) extends GeoJSON
  case class MultiPolygon(polygons: List[List[Point]]) extends GeoJSON
}