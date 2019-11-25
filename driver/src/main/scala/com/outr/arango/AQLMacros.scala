package com.outr.arango

import scala.annotation.compileTimeOnly
import scala.reflect.macros.blackbox
import scribe.Execution.global

import scala.concurrent.Await
import scala.concurrent.duration._

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object AQLMacros {
  def aql(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[Query] = {
    process(c)(validate = true, args = args)
  }
  def aqlu(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[Query] = {
    process(c)(validate = false, args = args)
  }
  def fieldAndValue(c: blackbox.Context)(value: c.Tree): c.Tree = {
    import c.universe._

    val extras = c.prefix.tree
    q"""
       import _root_.com.outr.arango.Value._
       _root_.com.outr.arango.FieldAndValue($extras.thisField, ${type2Value(c)(value)._1})
     """
  }

  private def type2Value(c: blackbox.Context)(value: c.Tree): (c.Tree, Boolean) = {
    import c.universe._

    val vt = value.tpe
    def n(t: c.Tree): (c.Tree, Boolean) = (t, false)
    if (vt <:< typeOf[Value]) {
      n(value)
    } else if (vt <:< typeOf[String]) {
      n(q"string($value)")
    } else if (vt <:< typeOf[Boolean]) {
      n(q"boolean($value)")
    } else if (vt <:< typeOf[Int]) {
      n(q"int($value)")
    } else if (vt <:< typeOf[Long]) {
      n(q"long($value)")
    } else if (vt <:< typeOf[Double]) {
      n(q"double($value)")
    } else if (vt <:< typeOf[BigDecimal]) {
      n(q"bigDecimal($value)")
    } else if (vt <:< typeOf[Option[String]]) {
      n(q"string($value)")
    } else if (vt <:< typeOf[Option[Boolean]]) {
      n(q"boolean($value)")
    } else if (vt <:< typeOf[Option[Int]]) {
      n(q"int($value)")
    } else if (vt <:< typeOf[Option[Long]]) {
      n(q"long($value)")
    } else if (vt <:< typeOf[Option[Double]]) {
      n(q"double($value)")
    } else if (vt <:< typeOf[Option[BigDecimal]]) {
      n(q"bigDecimal($value)")
    } else if (vt <:< typeOf[Null]) {
      n(q"Null")
    } else if (vt <:< typeOf[Seq[String]]) {
      n(q"strings($value)")
    } else if (vt <:< typeOf[Seq[Boolean]]) {
      n(q"booleans($value)")
    } else if (vt <:< typeOf[Seq[Int]]) {
      n(q"ints($value)")
    } else if (vt <:< typeOf[Seq[Long]]) {
      n(q"longs($value)")
    } else if (vt <:< typeOf[Seq[Double]]) {
      n(q"doubles($value)")
    } else if (vt <:< typeOf[Seq[BigDecimal]]) {
      n(q"bigDecimals($value)")
    } else if (vt <:< typeOf[Id[_]]) {
      n(q"string($value._id)")
    } else if (vt <:< typeOf[Field[_]]) {
      n(q"string($value.fieldName)")
    } else if (vt <:< typeOf[Analyzer]) {
      n(q"string($value.name)")
    } else if (vt <:< typeOf[Collection[_]]) {
      (q"string($value.name)", true)
    } else if (vt <:< typeOf[View[_]]) {
      (q"string($value.name)", true)
    } else {
      n(q"json(_root_.profig.JsonUtil.toJson[$vt]($value))")
    }
  }

  def process(c: blackbox.Context)(validate: Boolean, args: Seq[c.Expr[Any]]): c.Expr[Query] = {
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
              // TODO: use implicits instead?

              // TODO: Remove this once type2Value has been fully tested
              val queryArg = if (vt <:< typeOf[Value]) {
                Some(value.tree)
              } else if (vt <:< typeOf[String]) {
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
                Some(q"string($value.fieldName)")
              } else if (vt <:< typeOf[Analyzer]) {
                Some(q"string($value.name)")
              } else if (vt <:< typeOf[Collection[_]]) {
                special = true
                Some(q"string($value.name)")
              } else if (vt <:< typeOf[View[_]]) {
                special = true
                Some(q"string($value.name)")
              } else {
                Some(q"json(_root_.profig.JsonUtil.toJson[$vt]($value))")
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

        if (validate) {
          val db = new ArangoDB()
          val future = db.init().flatMap { _ =>
            db.api.db.validate(query)
          }
          future.onComplete(_ => db.dispose())

          val result = Await.result(future, 30.seconds)
          if (result.error) {
            c.abort(c.enclosingPosition, s"Error: ${result.errorMessage.get} (${result.errorCode}). Bad syntax for AQL query: $query.")
          }
        }
        c.Expr[Query](
          q"""
              import _root_.com.outr.arango.Value._
              import _root_.com.outr.arango.Query

              Query($query, $argsMap)
            """)
      }
      case _ => c.abort(c.enclosingPosition, "Bad usage of AQL interpolation.")
    }
  }
}
