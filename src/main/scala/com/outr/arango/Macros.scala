package com.outr.arango

import com.outr.Query

import scala.annotation.compileTimeOnly
import scala.concurrent.Await
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

        val url = Option(System.getenv("ARANGODB_URL")).getOrElse("http://localhost:8529")
        val username = Option(System.getenv("ARANGODB_USERNAME")).getOrElse("root")
        val password = Option(System.getenv("ARANGODB_PASSWORD")).getOrElse("root")
        val instance = new ArangoDB(url)
        val future = instance.auth(username, password).flatMap { session =>
          val db = session.db("test")
          db.parse(b.toString()).map(r => r.error -> r.bindVars)
        }
        val (error, bindVars) = Await.result(future, 30.seconds)
//        println("Sleeping...")
//        Thread.sleep(5000)
//        println("Done sleeping!")
        c.abort(c.enclosingPosition, s"Testing: $b, Error: $error, BindVars: $bindVars")
//        val parser = new CypherParser
//        parser.parse(b.toString())
//        c.Expr[CypherQuery](q"""CypherQuery(${b.toString()}, $argsMap)""")
      }
      case _ => c.abort(c.enclosingPosition, "Bad usage of cypher interpolation 2.")
    }
  }
}