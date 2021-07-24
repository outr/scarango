package com.outr.arango

import scala.annotation.compileTimeOnly
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object GraphMacros {
  def vertex[D <: Document[D]](c: blackbox.Context)()(implicit d: c.WeakTypeTag[D]): c.Expr[DocumentCollection[D]] = {
    import c.universe._

    val graph = c.prefix
    vertexOptions[D](c)(c.Expr[CollectionOptions](q"$graph.defaultCollectionOptions"))(d)
  }

  def vertexOptions[D <: Document[D]](c: blackbox.Context)(options: c.Expr[CollectionOptions])
                              (implicit d: c.WeakTypeTag[D]): c.Expr[DocumentCollection[D]] = {
    import c.universe._

    val graph = c.prefix
    val companion = d.tpe.typeSymbol.companion
    c.Expr[DocumentCollection[D]](
      q"""
         import com.outr.arango._

         new DocumentCollection[$d]($graph, $companion, CollectionType.Document, $companion.indexes, None, $options)
       """)
  }

  def edge[D <: Document[D]](c: blackbox.Context)()(implicit d: c.WeakTypeTag[D]): c.Expr[DocumentCollection[D]] = {
    import c.universe._

    val graph = c.prefix
    edgeOptions[D](c)(c.Expr[CollectionOptions](q"$graph.defaultCollectionOptions"))(d)
  }

  def edgeOptions[D <: Document[D]](c: blackbox.Context)(options: c.Expr[CollectionOptions])
                            (implicit d: c.WeakTypeTag[D]): c.Expr[DocumentCollection[D]] = {
    import c.universe._

    val graph = c.prefix
    val companion = d.tpe.typeSymbol.companion
    c.Expr[DocumentCollection[D]](
      q"""
         import com.outr.arango._

         new DocumentCollection[$d]($graph, $companion, CollectionType.Edge, $companion.indexes, None, $options)
       """)
  }
}