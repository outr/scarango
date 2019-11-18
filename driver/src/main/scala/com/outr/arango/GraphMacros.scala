package com.outr.arango

import scala.annotation.compileTimeOnly
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object GraphMacros {
  def store[T](c: blackbox.Context)
              (key: c.Expr[String])
              (implicit t: c.WeakTypeTag[T]): c.Expr[DatabaseStore[T]] = {
    import c.universe._

    val graph = c.prefix
    val tree =
      q"""
         DatabaseStore[$t]($key, $graph, Serialization.auto[$t])
       """
    c.Expr[DatabaseStore[T]](tree)
  }

  def queryBuilderAs[D](c: blackbox.Context)(implicit d: c.WeakTypeTag[D]): c.Expr[QueryBuilder[D]] = {
    import c.universe._

    val builder = c.prefix
    if (d.tpe <:< typeOf[Document[_]] && d.tpe.companion <:< typeOf[DocumentModel[_]]) {
      c.Expr[QueryBuilder[D]](q"$builder.as[$d](${d.tpe.typeSymbol.companion}.serialization)")
    } else {
      c.Expr[QueryBuilder[D]](q"$builder.as[$d](_root_.com.outr.arango.Serialization.auto[$d])")
    }
  }

  def vertex[D <: Document[D]](c: blackbox.Context)
                              (implicit d: c.WeakTypeTag[D]): c.Expr[Collection[D]] = {
    import c.universe._

    val graph = c.prefix
    val companion = d.tpe.typeSymbol.companion
    c.Expr[Collection[D]](
      q"""
         import com.outr.arango._

         new Collection[$d]($graph, $companion, CollectionType.Document, $companion.indexes, None)
       """)
  }

  def edge[D <: Document[D]](c: blackbox.Context)
                            (implicit d: c.WeakTypeTag[D]): c.Expr[Collection[D]] = {
    import c.universe._

    val graph = c.prefix
    val companion = d.tpe.typeSymbol.companion
    c.Expr[Collection[D]](
      q"""
         import com.outr.arango._

         new Collection[$d]($graph, $companion, CollectionType.Edge, $companion.indexes, None)
       """)
  }
}