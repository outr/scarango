package com.outr.arango

import scala.annotation.compileTimeOnly
import scala.reflect.macros.blackbox
import scribe.Execution.global

import scala.concurrent.Await
import scala.concurrent.duration._

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object AQLMacros {
  def aql(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[Query] = {
    import c.universe._

    // Make sure that Profig is initialized
    profig.Profig.loadDefaultsMacro()

    c.prefix.tree match {
      case Apply(_, List(Apply(_, rawParts))) => {
        val parts = rawParts map { case t @ Literal(Constant(const: String)) => (const, t.pos) }

        val b = new StringBuilder
        var argsMap = Map.empty[String, c.Expr[Value]]
        parts.zipWithIndex.foreach {
          case ((raw, _), index) => {
            if (index > 0) {
              var special = false
              var argName = s"arg$index"
              val value = args(index - 1)
              val vt = value.actualType
              val queryArg = if (vt <:< typeOf[String]) {
                Some(q"string($value)")
              } else if (vt <:< typeOf[Boolean]) {
                Some(q"boolean($value)")
              } else if (vt <:< typeOf[Int]) {
                Some(q"int($value)")
              } else if (vt <:< typeOf[Long]) {
                Some(q"long($value)")
              } else if (vt <:< typeOf[Double]) {
                Some(q"double($value)")
              } else if (vt <:< typeOf[BigDecimal]) {
                Some(q"bigDecimal($value)")
              } else if (vt <:< typeOf[Option[String]]) {
                Some(q"string($value)")
              } else if (vt <:< typeOf[Option[Boolean]]) {
                Some(q"boolean($value)")
              } else if (vt <:< typeOf[Option[Int]]) {
                Some(q"int($value)")
              } else if (vt <:< typeOf[Option[Long]]) {
                Some(q"long($value)")
              } else if (vt <:< typeOf[Option[Double]]) {
                Some(q"double($value)")
              } else if (vt <:< typeOf[Option[BigDecimal]]) {
                Some(q"bigDecimal($value)")
              } else if (vt <:< typeOf[Null]) {
                Some(q"Null")
              } else if (vt <:< typeOf[Seq[String]]) {
                Some(q"strings($value)")
              } else if (vt <:< typeOf[Seq[Boolean]]) {
                Some(q"booleans($value)")
              } else if (vt <:< typeOf[Seq[Int]]) {
                Some(q"ints($value)")
              } else if (vt <:< typeOf[Seq[Long]]) {
                Some(q"longs($value)")
              } else if (vt <:< typeOf[Seq[Double]]) {
                Some(q"doubles($value)")
              } else if (vt <:< typeOf[Seq[BigDecimal]]) {
                Some(q"bigDecimals($value)")
              } else if (vt <:< typeOf[Id[_]]) {
                Some(q"string($value._id)")
              } else if (vt <:< typeOf[Field[_]]) {
                c.abort(c.enclosingPosition, s"Field needs fully-qualified name! $value")
              } else if (vt <:< typeOf[Collection[_]]) {
                special = true
                Some(q"string($value.name)")
              } else {
                c.abort(c.enclosingPosition, s"Unsupported Value: $vt.")
              }
              if (special) {
                argName = s"@$argName"
              }
              queryArg.foreach { arg =>
                b.append(s"@$argName")
                argsMap += argName -> c.Expr[Value](arg)
              }
            }
            b.append(raw)
          }
        }

        val query = b.toString().trim

        val db = new ArangoDB()
        val future = db.init().flatMap { _ =>
          db.api.db.validate(query)
        }
        future.onComplete(_ => db.dispose())

        val result = Await.result(future, 30.seconds)
        if (result.error) {
          c.abort(c.enclosingPosition, s"Error: ${result.errorMessage.get} (${result.errorCode}). Bad syntax for AQL query: $query.")
        }
        c.Expr[Query](
          q"""
              import _root_.com.outr.arango.Value._
              import _root_.com.outr.arango.Query

              Query($query, $argsMap)
            """)
      }
      case _ => c.abort(c.enclosingPosition, "Bad usage of cypher interpolation.")
    }
  }
}
