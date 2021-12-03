package spec

import com.outr.arango.{Query, QueryPart}
import com.outr.arango.core._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QuerySpec extends AnyWordSpec with Matchers {
  "Query" should {
    "convert a static scenario properly" in {
      val q = Query("FOR t IN test RETURN t")
      q.parts should be(List(QueryPart.Static("FOR t IN test RETURN t")))
      q.variables should be(Map.empty)
      q.string should be("FOR t IN test RETURN t")
    }
    "convert a simple scenario properly" in {
      val q = Query(List(
        QueryPart.Static("FOR t IN test FILTER t.name == "),
        QueryPart.Variable(fabric.Str("Matt")),
        QueryPart.Static(" RETURN t")
      ))
      q.variables should be(Map("arg0" -> fabric.Str("Matt")))
      q.string should be("FOR t IN test FILTER t.name == @arg0 RETURN t")
    }
    "convert a reused variable properly" in {
      val q = Query(List(
        QueryPart.Static("FOR t IN test FILTER t.name == "),
        QueryPart.Variable(fabric.Str("Matt")),
        QueryPart.Static(" || t.firstName == "),
        QueryPart.Variable(fabric.Str("Matt")),
        QueryPart.Static(" RETURN t")
      ))
      q.variables should be(Map("arg0" -> fabric.Str("Matt")))
      q.string should be("FOR t IN test FILTER t.name == @arg0 || t.firstName == @arg0 RETURN t")
    }
    "convert a reused variable properly with the DSL" in {
      val q = Query(
        "FOR t IN test FILTER t.name == ",
        fabric.Str("Matt"),
        " || t.firstName == ",
        "arg0" -> fabric.Str("Matt"),
        " RETURN t"
      )
      q.variables should be(Map("arg0" -> fabric.Str("Matt")))
      q.string should be("FOR t IN test FILTER t.name == @arg0 || t.firstName == @arg0 RETURN t")
    }
    "convert a simple scenario with a named variable" in {
      val q = Query(List(
        QueryPart.Static("FOR t IN test FILTER t.name == "),
        QueryPart.NamedVariable("name", fabric.Str("Matt")),
        QueryPart.Static(" RETURN t")
      ))
      q.variables should be(Map("name" -> fabric.Str("Matt")))
      q.string should be("FOR t IN test FILTER t.name == @name RETURN t")
    }
    "error when using the same name with different named variable values" in {
      val q = Query(List(
        QueryPart.Static("FOR t IN test FILTER t.name == "),
        QueryPart.NamedVariable("name", fabric.Str("Adam")),
        QueryPart.Static(" || t.firstName == "),
        QueryPart.NamedVariable("name", fabric.Str("Bob")),
        QueryPart.Static(" RETURN t")
      ))
      val caught = intercept[RuntimeException] {
        q.variables should be(Map("arg0" -> fabric.Str("Matt")))
      }
      caught.getMessage should be("""Duplicate named variable with different values: name with "Bob" and "Adam"""")
    }
  }
}
