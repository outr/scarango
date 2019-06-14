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
                c.Expr[Value](q"com.outr.arango.Value.string($value)")
              } else if (vt <:< typeOf[Boolean]) {
                c.Expr[Value](q"com.outr.arango.Value.boolean($value)")
              } else if (vt <:< typeOf[Int]) {
                c.Expr[Value](q"com.outr.arango.Value.int($value)")
              } else if (vt <:< typeOf[Long]) {
                c.Expr[Value](q"com.outr.arango.Value.long($value)")
              } else if (vt <:< typeOf[Double]) {
                c.Expr[Value](q"com.outr.arango.Value.double($value)")
              } else if (vt <:< typeOf[BigDecimal]) {
                c.Expr[Value](q"com.outr.arango.Value.bigDecimal($value)")
              } else if (vt <:< typeOf[Option[String]]) {
                c.Expr[Value](q"com.outr.arango.Value.string($value)")
              } else if (vt <:< typeOf[Option[Boolean]]) {
                c.Expr[Value](q"com.outr.arango.Value.boolean($value)")
              } else if (vt <:< typeOf[Option[Int]]) {
                c.Expr[Value](q"com.outr.arango.Value.int($value)")
              } else if (vt <:< typeOf[Option[Long]]) {
                c.Expr[Value](q"com.outr.arango.Value.long($value)")
              } else if (vt <:< typeOf[Option[Double]]) {
                c.Expr[Value](q"com.outr.arango.Value.double($value)")
              } else if (vt <:< typeOf[Option[BigDecimal]]) {
                c.Expr[Value](q"com.outr.arango.Value.bigDecimal($value)")
              } else if (vt <:< typeOf[Null]) {
                c.Expr[Value](q"com.outr.arango.Value.Null")
              } else if (vt <:< typeOf[Seq[String]]) {
                c.Expr[Value](q"com.outr.arango.Value.strings($value)")
              } else if (vt <:< typeOf[Seq[Boolean]]) {
                c.Expr[Value](q"com.outr.arango.Value.booleans($value)")
              } else if (vt <:< typeOf[Seq[Int]]) {
                c.Expr[Value](q"com.outr.arango.Value.ints($value)")
              } else if (vt <:< typeOf[Seq[Long]]) {
                c.Expr[Value](q"com.outr.arango.Value.longs($value)")
              } else if (vt <:< typeOf[Seq[Double]]) {
                c.Expr[Value](q"com.outr.arango.Value.doubles($value)")
              } else if (vt <:< typeOf[Seq[BigDecimal]]) {
                c.Expr[Value](q"com.outr.arango.Value.bigDecimals($value)")
              } else if (vt <:< typeOf[Id[_]]) {
                c.Expr[Value](q"com.outr.arango.Value.string($value._id)")
              } else if (vt <:< typeOf[Field[_]]) {
                special = true
                c.Expr[Value](q"com.outr.arango.Value.string($value.name)")
              } else if (vt <:< typeOf[Collection[_]]) {
                special = true
                c.Expr[Value](q"com.outr.arango.Value.string($value.name)")
                //              } else if (vt <:< typeOf[com.outr.arango.managed.VertexCollection[_]]) {
                //                special = true
                //                c.Expr[Value](q"com.outr.arango.Value.string($value.name)")
                //              } else if (vt <:< typeOf[com.outr.arango.managed.EdgeCollection[_]]) {
                //                special = true
                //                c.Expr[Value](q"com.outr.arango.Value.string($value.name)")
              } else {
                c.abort(c.enclosingPosition, s"Unsupported Value: $vt.")
              }
              if (special) {
                argName = s"@$argName"
              }
              b.append(s"@$argName")
              argsMap += argName -> queryArg
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
        c.Expr[Query](q"""com.outr.arango.Query($query, $argsMap)""")
      }
      case _ => c.abort(c.enclosingPosition, "Bad usage of cypher interpolation.")
    }
  }
}
