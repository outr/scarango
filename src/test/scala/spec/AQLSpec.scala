package spec

import com.outr.arango._
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._

import scala.concurrent.Future

class AQLSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var users: ArangoCollection = _

//  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  "AQL" when {
    "doing simple parser check" should {
      "create the session" in {
        ArangoSession.default.map { s =>
          session = s
          db = session.db("_system")
          users = db.collection("users")
          s.token shouldNot be("")
        }
      }
      "parse a simple query with no args successfully" in {
        session.parse("FOR user IN users RETURN user").map { parseResult =>
          parseResult.error should be(false)
        }
      }
      "parse an invalid query with no args successfully" in {
        session.parse("FOR user IN users RETURNING user").map { parseResult =>
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
    "querying" should {
      "create the users collection" in {
        users.create(waitForSync = true).map { response =>
          response.error should be(false)
        }
      }
      "insert a user" in {
        users.document.create(User("John Doe", 21)).map { result =>
          result._id shouldNot be(None)
        }
      }
//      "handle a simple query" in {
//        val query = aql"FOR user IN users RETURN user"
//        db.cursor[User](query).map { response =>
//          println(response.result.headOption)
//          response.result.size should be(1)
//        }
//      }
    }
    "cleanup" should {
      "drop the users collection" in {
        users.drop().map { response =>
          response.error should be(false)
        }
      }
      "dispose the session" in {
        Future {
          session.instance.dispose()
          session.instance.isDisposed should be(true)
        }
      }
    }
  }
}
