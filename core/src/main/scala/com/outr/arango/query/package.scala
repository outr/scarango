package com.outr.arango

import fabric._

import scala.language.implicitConversions

package object query {
  implicit class AQLInterpolator(val sc: StringContext) extends AnyVal {
    def aql(args: Any*): Query = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      var parts = List.empty[QueryPart]
      while (strings.hasNext || expressions.hasNext) {
        if (strings.hasNext) {
          parts = QueryPart.Static(strings.next()) :: parts
        }
        if (expressions.hasNext) {
          val part = toQueryPart(expressions.next())
          parts = part :: parts
        }
      }
      Query(parts.reverse)
    }
  }

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
    case l: Long => num(l.toDouble)
  }
}