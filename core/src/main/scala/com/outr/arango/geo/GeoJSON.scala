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
    GeoPoint.rw, GeoMultiPoint.rw, GeoLineString.rw, GeoMultiLineString.rw, GeoPolygon.rw, GeoMultiPolygon.rw
  )
}

case class GeoPoint(latitude: Double, longitude: Double) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_POINT($longitude, $latitude)")
}

object GeoPoint {
  implicit val rw: RW[GeoPoint] = createRW[GeoPoint](
    point => pointArray(point),
    pointFromCoords,
    dimensions = 1,
    name = "Point"
  ).withPostRead(addType("Point"))
}

case class GeoMultiPoint(points: List[GeoPoint]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_MULTIPOINT(${points.map(pointArray).json})")
}

object GeoMultiPoint {
  implicit val rw: RW[GeoMultiPoint] = createRW[GeoMultiPoint](
    mp => mp.points.map(pointArray).json,
    json => GeoMultiPoint(pointsFromCoords(json)),
    dimensions = 2,
    name = "MultiPoint"
  ).withPostRead(addType("MultiPoint"))
}

case class GeoLineString(points: List[GeoPoint]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_LINESTRING(${points.map(pointArray).json})")
}

object GeoLineString {
  implicit val rw: RW[GeoLineString] = createRW[GeoLineString](
    ls => ls.points.map(pointArray).json,
    json => GeoLineString(pointsFromCoords(json)),
    dimensions = 2,
    name = "LineString"
  ).withPostRead(addType("LineString"))
}

case class GeoMultiLineString(lines: List[List[GeoPoint]]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_MULTILINESTRING(${lines.map(_.map(pointArray)).json})")
}

object GeoMultiLineString {
  implicit val rw: RW[GeoMultiLineString] = createRW[GeoMultiLineString](
    mls => mls.lines.map(_.map(pointArray)).json,
    json => GeoMultiLineString(multiPointsFromCoords(json)),
    dimensions = 3,
    name = "MultiLineString"
  ).withPostRead(addType("MultiLineString"))
}

case class GeoPolygon(points: List[GeoPoint]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_POLYGON(${points.map(pointArray).json})")
}

object GeoPolygon {
  implicit val rw: RW[GeoPolygon] = createRW[GeoPolygon](
    p => p.points.map(pointArray).json,
    json => GeoPolygon(pointsFromCoords(json)),
    dimensions = 3,
    name = "Polygon"
  ).withPostRead(addType("Polygon"))
}

case class GeoMultiPolygon(polygons: List[List[GeoPoint]]) extends GeoJSON {
  override def asQueryPart: QueryPart = QueryPart.Static(s"GEO_MULTIPOLYGON(${polygons.map(_.map(pointArray)).json})")
}

object GeoMultiPolygon {
  implicit val rw: RW[GeoMultiPolygon] = createRW[GeoMultiPolygon](
    mp => mp.polygons.map(_.map(pointArray)).json,
    json => GeoMultiPolygon(multiPointsFromCoords(json)),
    dimensions = 4,
    name = "MultiPolygon"
  ).withPostRead(addType("MultiPolygon"))
}