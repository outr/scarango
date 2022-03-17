package com.outr.arango

import fabric._

import scala.language.implicitConversions

package object query {
  implicit def sc2AQL(sc: StringContext): AQLInterpolator = new AQLInterpolator(sc)

  implicit def string2QueryPart(s: String): QueryPart = QueryPart.Static(s)
  implicit def value2QueryPart(v: fabric.Value): QueryPart = QueryPart.Variable(v)
  implicit def tuple2QueryPart(t: (String, fabric.Value)): QueryPart = QueryPart.NamedVariable(t._1, t._2)

  def toQueryPart(value: Any): QueryPart = value match {
    case null => QueryPart.Variable(Null)
    case i: Int => QueryPart.Variable(toValue(i))
    case s: String => QueryPart.Variable(toValue(s))
    case id: Id[_] => QueryPart.Variable(toValue(id._id))
    case qp: QueryPart => qp
    case seq: Seq[_] => QueryPart.Variable(arr(seq.map(toValue): _*))
    case _ => throw new RuntimeException(s"Unsupported expression: $value (${value.getClass.getName})")
  }

  def toValue(value: Any): Value = value match {
    case null => Null
    case s: String => str(s)
    case i: Int => num(i)
    case l: Long => num(l)
    case f: Float => num(f.toDouble)
    case d: Double => num(d)
  }
}