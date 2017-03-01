package com.outr.arango

import com.outr.Query
import org.powerscala.io._

import scala.annotation.compileTimeOnly
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.concurrent.ExecutionContext.Implicits.global

@compileTimeOnly("Enable macro paradise to expand compile-time macros")
object Macros {
  def aql(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[Query] = {
    import c.universe._

    c.prefix.tree match {
      case Apply(_, List(Apply(_, rawParts))) => {
        val parts = rawParts map { case t @ Literal(Constant(const: String)) => (const, t.pos) }

        val b = new StringBuilder
        parts.zipWithIndex.foreach {
          case ((raw, _), index) => {
            if (index > 0) {
              b.append(s"@arg$index")
            }
            b.append(raw)
          }
        }
        val argsMap = args.zipWithIndex.map {
          case (value, index) => {
            s"arg${index + 1}" -> value
          }
        }.toMap

        val url = Option(System.getenv("ARANGO_URL")).getOrElse("http://localhost:8529")
        val username = Option(System.getenv("ARANGO_USERNAME")).getOrElse("root")
        val password = Option(System.getenv("ARANGO_PASSWORD")).getOrElse("root")
        val instance = new ArangoDB(url)
        try {
          val query = b.toString()
          val future = instance.auth(username, password).flatMap { session =>
            session.parse(query)
          }
          val result = Await.result(future, 30.seconds)
          if (result.error) {
            c.abort(c.enclosingPosition, s"Error #${result.code}. Bad syntax for AQL query: $query.")
          }
          // TODO: return a Query
          c.abort(c.enclosingPosition, s"Testing: $b, Error: ${result.error}, BindVars: ${result.bindVars}, $result")
        } finally {
          instance.dispose()
        }
      }
      case _ => c.abort(c.enclosingPosition, "Bad usage of cypher interpolation.")
    }
  }
}