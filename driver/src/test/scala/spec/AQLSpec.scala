package spec

import com.outr.arango._
import io.youi.http.Headers
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

class AQLSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()
  private lazy val dbExample = db.api.db("aqlExample")
  private lazy val collection = dbExample.collection(User.collectionName)
  private implicit val serialization: Serialization[User] = User.serialization

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
      db.api.db.validate("FOR user IN users RETURN user").map { parseResult =>
        parseResult.error should be(false)
      }
    }
    "parse an invalid query with no args successfully" in {
     db.api.db.validate("FOR user IN users RETURNING user").map { parseResult =>
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
    "create the database" in {
      dbExample.create().map { response =>
        response.value should be(true)
      }
    }
    "create a new collection" in {
      collection.create(waitForSync = Some(true)).map { info =>
        info.name should be(Some(User.collectionName))
      }
    }
    "insert a user" in {
      collection.document.insertOne(User("John Doe", 21)).map { insert =>
        insert._id should not be null
      }
    }
    "handle a simple query" in {
      val query = aql"FOR user IN users RETURN user"
      dbExample.query(query).as[User].cursor.map { response =>
        response.id should be(None)
        response.result.size should be(1)
        val user = response.result.head
        user.name should be("John Doe")
        user.age should be(21)
        user._id should not be null
      }
    }
    "verify `first` returns the first entry" in {
      val query = aql"FOR user IN users RETURN user"
      dbExample.query(query).as[User].first.map { userOption =>
        userOption shouldNot be(None)
        val user = userOption.get
        user.name should be("John Doe")
        user.age should be(21)
        user._id should not be null
      }
    }
    "insert another user" in {
      collection.document.insertOne(User("Jane Doe", 20, Some("Online"))).map { insert =>
        insert._id should not be null
      }
    }
    "handle a two page cursor call" in {
      val query = aql"FOR user IN users RETURN user"
      val b = dbExample.query(query).as[User].includeCount.batchSize(1)
      b.cursor.map { response =>
        response.result.size should be(1)
        response.count should be(2)
        response.id shouldNot be(None)

        response.id.get
      }.flatMap { id =>
        b.get(id).map { response =>
          response.result.size should be(1)
          response.count should be(2)
        }
      }
    }
    "find a user from a list of names" in {
      val names = List("John Doe")
      val query = aql"FOR user IN users FILTER user.name IN $names RETURN user"
      dbExample.query(query).as[User].includeCount.cursor.map { result =>
        result.count should be(1)
        val userOption = result.result.headOption
        userOption shouldNot be(None)
        val user = userOption.get
        user.name should be("John Doe")
        user.age should be(21)
        user._id should not be null
      }
    }
    "find a user from a list of ages" in {
      val ages = List(21)
      val query = aql"FOR user IN users FILTER user.age IN $ages RETURN user"
      dbExample.query(query).as[User].includeCount.cursor.map { result =>
        result.count should be(1)
        val userOption = result.result.headOption
        userOption shouldNot be(None)
        val user = userOption.get
        user.name should be("John Doe")
        user.age should be(21)
        user._id should not be null
      }
    }
    "find a user where status is null" in {
      val status: String = null
      val query = aql"FOR user IN users FILTER user.status == $status RETURN user"
      dbExample.query(query).as[User].includeCount.cursor.map { result =>
        result.count should be(1)
        val userOption = result.result.headOption
        userOption shouldNot be(None)
        val user = userOption.get
        user.name should be("John Doe")
        user.age should be(21)
        user._id should not be null
      }
    }
    "find a user where status is None" in {
      val status: Option[String] = None
      val query = aql"FOR user IN users FILTER user.status == $status RETURN user"
      dbExample.query(query).as[User].includeCount.cursor.map { result =>
        result.count should be(1)
        val userOption = result.result.headOption
        userOption shouldNot be(None)
        val user = userOption.get
        user.name should be("John Doe")
        user.age should be(21)
        user._id should not be null
      }
    }
    "drop the test database" in {
      dbExample.drop().map { response =>
        response.value should be(true)
      }
    }
  }

  case class User(name: String,
                  age: Int,
                  status: Option[String] = None,
                  _id: Id[User] = User.id()) extends Document[User]

  object User extends DocumentModel[User] {
    override val collectionName: String = "users"
    override implicit val serialization: Serialization[User] = Serialization.auto[User]
  }
}
