package spec

import com.outr.arango.{ArangoCollection, ArangoDB, ArangoSession}
import io.circe.Encoder
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveEncoder

import scala.concurrent.Future

class CollectionSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var test: ArangoCollection = _

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  "Collections" should {
    "create the session" in {
      ArangoSession.default.map { s =>
        session = s
        db = session.db("_system")
        test = db.collection("test")
        s.token shouldNot be("")
      }
    }
    "create a new collection" in {
      test.create(waitForSync = true).map { response =>
        response.error should be(false)
      }
    }
    "insert a document" in {
      test.document.create(User("John Doe", 30), returnNew = true).map { response =>
        response.`new` shouldNot be(None)
        val user = response.`new`.head
        user.name should be("John Doe")
        user.age should be(30)
        user._id shouldNot be(None)
        user._key shouldNot be(None)
        user._rev shouldNot be(None)
      }
    }
    "get collection information" in {
      test.information().map { response =>
        response.name should be("test")
        response.`type` should be(2)
        response.status should be(3)
      }
    }
    "get collection properties" in {
      test.properties().map { response =>
        response.waitForSync should be(true)
      }
    }
    "get collection count" in {
      test.count().map { response =>
        response.name should be("test")
        response.`type` should be(2)
        response.status should be(3)
        response.count should be(1)
      }
    }
    "get collection revision" in {
      test.revision().map { response =>
        response.name should be("test")
        response.`type` should be(2)
        response.status should be(3)
        response.revision shouldNot be("0")
      }
    }
    "list all collections" in {
      test.list().map { response =>
        val collectionNames = response.result.map(_.name).toSet
        collectionNames should contain("test")
      }
    }
    "truncate the collection" in {
      test.truncate().map { response =>
        response.error should be(false)
      }
    }
    "drop the new collection" in {
      test.drop().map { response =>
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

case class User(name: String,
                age: Int,
                _key: Option[String] = None,
                _id: Option[String] = None,
                _rev: Option[String] = None)