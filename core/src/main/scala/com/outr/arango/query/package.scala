package com.outr.arango

import fabric._
import fabric.rw._

import scala.language.implicitConversions

package object query {
  implicit def sc2AQL(sc: StringContext): AQLInterpolator = new AQLInterpolator(sc)
  implicit def rw2QueryPart[T: RW](t: T): QueryPart = value2QueryPart(t.json)
  implicit def value2QueryPart(value: Json): QueryPart = QueryPart.Variable(value)
  implicit def tuple2QueryPart(t: (String, Json)): QueryPart = QueryPart.NamedVariable(t._1, t._2)
}