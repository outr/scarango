package spec

import com.outr.arango._
import org.scalatest.{AsyncWordSpec, Matchers, WordSpec}

class AQLSpec extends AsyncWordSpec with Matchers {
  "AQL" when {
    "doing simple parser check" should {
      val session = ArangoSession.default

      "parse a simple query with no args successfully" in {
        session.flatMap { s =>
          s.parse("FOR user IN users RETURN user")
        }.map { parseResult =>
          parseResult.error should be(false)
        }
      }
      "parse an invalid query with no args successfully" in {
        session.flatMap { s =>
          s.parse("FOR user IN users RETURNING user")
        }.map { parseResult =>
          parseResult.error should be(true)
        }
      }
    }
    "doing compile-time interpolation" should {
      "parse a simple query with no args successfully" in {
        val query = aql"FOR user IN users RETURN user"
        query.value should be("FOR user IN users RETURN user")
        query.args should be(Map.empty)
      }
      "parse a simple query with two args successfully" in {
        val id = 123
        val name = "John Doe"
        val query = aql"FOR u IN users FILTER u.id == $id && u.name == $name RETURN u"
        query.value should be("FOR u IN users FILTER u.id == @arg1 && u.name == @arg2 RETURN u")
        query.args should be(Map("arg1" -> QueryArg.int(123), "arg2" -> QueryArg.string("John Doe")))
      }
    }
  }
}
