package com.outr.arango

import fabric._

import scala.language.implicitConversions

package object query {
  implicit def sc2AQL(sc: StringContext): AQLInterpolator = new AQLInterpolator(sc)

  implicit def string2QueryPart(s: String): QueryPart = QueryPart.Static(s)
  implicit def value2QueryPart(v: fabric.Value): QueryPart = QueryPart.Variable(v)
  implicit def tuple2QueryPart(t: (String, fabric.Value)): QueryPart = QueryPart.NamedVariable(t._1, t._2)

  def toQueryPart(value: Any): QueryPart = value match {
    case qp: QueryPart => qp
    case _ => QueryPart.Variable(toValue(value))
  }

  def toValue(value: Any): Value = value match {
    case null | None => Null
    case Some(v) => toValue(v)
    case v: Value => v
    case id: Id[_] => str(id._id)
    case s: String => str(s)
    case b: Boolean => bool(b)
    case i: Int => num(i)
    case l: Long => num(l)
    case f: Float => num(f.toDouble)
    case d: Double => num(d)
    case seq: Seq[_] => arr(seq.map(toValue): _*)
    case _ => throw new RuntimeException(s"Unsupported expression: $value (${value.getClass.getName})")
  }
}