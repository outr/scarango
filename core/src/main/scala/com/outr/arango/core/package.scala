package com.outr.arango

import scala.language.implicitConversions

package object core {
  implicit def string2QueryPart(s: String): QueryPart = QueryPart.Static(s)
  implicit def value2QueryPart(v: fabric.Value): QueryPart = QueryPart.Variable(v)
  implicit def tuple2QueryPart(t: (String, fabric.Value)): QueryPart = QueryPart.NamedVariable(t._1, t._2)
}