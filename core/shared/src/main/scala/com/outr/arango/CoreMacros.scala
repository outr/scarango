package com.outr.arango

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object CoreMacros {
  def updateIfModifiable[T](c: blackbox.Context)(value: c.Expr[T])(implicit t: c.WeakTypeTag[T]): c.Expr[T] = {
    import c.universe._

    if (t.tpe <:< typeOf[Modifiable]) {
      c.Expr[T](q"$value.copy(modified = System.currentTimeMillis())")
    } else {
      c.Expr[T](q"$value")
    }
  }
}