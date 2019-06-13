package spec

import com.outr.arango._
import io.youi.http.Headers
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

class AQLSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "AQL" should {
    "initialize configuration" in {
      Profig.loadDefaults()
      succeed
    }
    "initialize successfully" in {
      db.init().map { state =>
        state shouldBe a[DatabaseState.Initialized]
        db.session.client.request.headers.first(Headers.Request.Authorization) should not be None
      }
    }
    "parse a simple query with no args successfully" in {
      db.api.db.query.validate("FOR user IN users RETURN user").map { parseResult =>
        parseResult.error should be(false)
      }
    }
    "parse an invalid query with no args successfully" in {
     db.api.db.query.validate("FOR user IN users RETURNING user").map { parseResult =>
        parseResult.error should be(true)
      }
    }
    "interpolate a simple query with no args successfully" in {
      val query = aql"FOR user IN users RETURN user"
      query.value should be("FOR user IN users RETURN user")
      query.args should be(Map.empty)
    }
    "interpolate a simple query with two args successfully" in {
      val id = 123
      val name = "John Doe"
      val query = aql"FOR u IN users FILTER u.id == $id && u.name == $name RETURN u"
      query.value should be("FOR u IN users FILTER u.id == @arg1 && u.name == @arg2 RETURN u")
      query.args should be(Map("arg1" -> Value.int(123), "arg2" -> Value.string("John Doe")))
    }
  }
}
