package com.outr.arango

import scala.annotation.compileTimeOnly
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object Macros {
  def serializationAuto[D](c: blackbox.Context)
                          (implicit d: c.WeakTypeTag[D]): c.Expr[Serialization[D]] = {
    import c.universe._

    c.Expr[Serialization[D]](
      q"""
         import _root_.com.outr.arango._
         import _root_.profig._
         import _root_.io.circe.Json

         Serialization[$d](
           doc2Json = (d: $d) => JsonUtil.toJson[$d](d),
           json2Doc = (j: Json) => JsonUtil.fromJson[$d](j)
         )
       """)
  }
}
