package com.outr.arango.geo

import com.outr.arango.geo.GeoJSON._
import com.outr.arango.query.QueryPart
import fabric._
import fabric.define.DefType
import fabric.rw._

import scala.language.implicitConversions

sealed trait GeoJSON {
  def asQueryPart: QueryPart
}

object GeoJSON {
  implicit def asQueryPart(geo: GeoJSON): QueryPart = geo.asQueryPart

  private[geo] def addType[T <: GeoJSON](name: String)(t: T, json: Json): Json = json.merge(obj(
    "type" -> name
  ))
  private[geo] def pointArray(p: Point): Json = arr(p.longitude, p.latitude)
  private[geo] def pointFromCoords(json: Json): Point = {
    val arr = json.asArr
    Point(
      latitude = arr.value(1).asDouble,
      longitude = arr.value(0).asDouble
    )
  }
  private[geo] def pointsFromCoords(json: Json): List[Point] = json.asArr.value.toList.map(pointFromCoords)
  private[geo] def multiPointsFromCoords(json: Json): List[List[Point]] = json.asArr.value.toList.map(pointsFromCoords)
  private[geo] def createRW[T <: GeoJSON](toCoordinates: T => Json,
                                          fromCoordinates: Json => T,
                                          dimensions: Int,
                                          name: String): RW[T] = {
    def d(dim: Int): DefType = if (dim == 0) {
      DefType.Dec
    } else {
      DefType.Arr(d(dim - 1))
    }
    RW.from(
      r = t => obj(
        "coordinates" -> toCoordinates(t)
      ),
      w = j => fromCoordinates(j("coordinates")),
      d = DefType.Obj(Some(s"com.outr.arango.geo.$name"), "coordinates" -> d(dimensions))
    )
  }

  implicit lazy val rw: RW[GeoJSON] = RW.poly[GeoJSON]()(
    Point.rw, MultiPoint.rw, LineString.rw, MultiLineString.rw, Polygon.rw, MultiPolygon.rw
  )
}

case class Point(latitude: Double, longitude: Double) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_POINT($longitude, $latitude)")
}

object Point {
  implicit val rw: RW[Point] = createRW[Point](
    point => pointArray(point),
    pointFromCoords,
    dimensions = 1,
    name = "Point"
  ).withPostRead(addType("Point"))
}

case class MultiPoint(points: List[Point]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_MULTIPOINT(${points.map(pointArray).json})")
}

object MultiPoint {
  implicit val rw: RW[MultiPoint] = createRW[MultiPoint](
    mp => mp.points.map(pointArray).json,
    json => MultiPoint(pointsFromCoords(json)),
    dimensions = 2,
    name = "MultiPoint"
  ).withPostRead(addType("MultiPoint"))
}

case class LineString(points: List[Point]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_LINESTRING(${points.map(pointArray).json})")
}

object LineString {
  implicit val rw: RW[LineString] = createRW[LineString](
    ls => ls.points.map(pointArray).json,
    json => LineString(pointsFromCoords(json)),
    dimensions = 2,
    name = "LineString"
  ).withPostRead(addType("LineString"))
}

case class MultiLineString(lines: List[List[Point]]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_MULTILINESTRING(${lines.map(_.map(pointArray)).json})")
}

object MultiLineString {
  implicit val rw: RW[MultiLineString] = createRW[MultiLineString](
    mls => mls.lines.map(_.map(pointArray)).json,
    json => MultiLineString(multiPointsFromCoords(json)),
    dimensions = 3,
    name = "MultiLineString"
  ).withPostRead(addType("MultiLineString"))
}

case class Polygon(points: List[Point]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_POLYGON(${points.map(pointArray).json})")
}

object Polygon {
  implicit val rw: RW[Polygon] = createRW[Polygon](
    p => p.points.map(pointArray).json,
    json => Polygon(pointsFromCoords(json)),
    dimensions = 3,
    name = "Polygon"
  ).withPostRead(addType("Polygon"))
}

case class MultiPolygon(polygons: List[List[Point]]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_MULTIPOLYGON(${polygons.map(_.map(pointArray)).json})")
}

object MultiPolygon {
  implicit val rw: RW[MultiPolygon] = createRW[MultiPolygon](
    mp => mp.polygons.map(_.map(pointArray)).json,
    json => MultiPolygon(multiPointsFromCoords(json)),
    dimensions = 4,
    name = "MultiPolygon"
  ).withPostRead(addType("MultiPolygon"))
}