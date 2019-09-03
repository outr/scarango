package com.outr.arango.transaction

import com.outr.arango.Graph

import scala.annotation.compileTimeOnly
import scala.concurrent.Future
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object TransactionMacros {
  def simple[G <: Graph, R](c: blackbox.Context)
                                (transaction: c.Expr[(G) => Future[R]])
                                (implicit g: c.WeakTypeTag[G]): c.Expr[Future[R]] = {
    import c.universe._

    c.Expr[Future[R]](
      q"""
        import com.outr.arango.transaction._
        import scribe.Execution.global


       """)

    ???
  }
}
