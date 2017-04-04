package spec

import com.outr.arango._
import com.outr.arango.dsl._
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._

import scala.concurrent.Future

class AQLSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var users: ArangoCollection = _

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]

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
    "dsl" should {
      "build a simple query" in {
        val query = FOR ("user") IN "users" RETURN "user"
        query.parts.length should be(2)
        val forPart = query.parts.head
        forPart should be(ForPart(List("user"), "users"))
        val returnPart = query.parts.tail.head
        returnPart should be(ReturnPart(List("user")))
      }
      /*"build a simple query with two args" in {
        val id = 123
        val name = "John Doe"
        val query = FOR ("u") IN "users" FILTER ("u.id" === QueryArg.int(id), "u.name" === QueryArg.string(name)) RETURN "u"
        query shouldNot be(null)
      }*/
    }
    "querying" should {
      "verify the collection doesn't already exist" in {
        users.exists().map { result =>
          result should be(None)
        }
      }
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
      "handle a simple query" in {
        val query = aql"FOR user IN users RETURN user"
        db.cursor[User](query).map { response =>
          response.id should be(None)
          response.result.size should be(1)
          val user = response.result.head
          user.name should be("John Doe")
          user.age should be(21)
          user._id shouldNot be(None)
          user._key shouldNot be(None)
          user._rev shouldNot be(None)
        }
      }
      "insert another user" in {
        users.document.create(User("Jane Doe", 20)).map { result =>
          result._id shouldNot be(None)
        }
      }
      "handle a two page cursor call" in {
        val query = aql"FOR user IN users RETURN user"
        db.cursor[User](query, count = true, batchSize = Some(1)).map { response =>
          response.result.size should be(1)
          response.count should be(Some(2))
          response.id shouldNot be(None)

          response.id.get
        }.flatMap { id =>
          db.cursor.get[User](id).map { response =>
            response.result.size should be(1)
            response.count should be(Some(2))
          }
        }
      }
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
