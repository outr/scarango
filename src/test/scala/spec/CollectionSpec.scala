package spec

import com.outr.arango.{ArangoCollection, ArangoDB, ArangoSession}
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe.generic.auto._

import scala.concurrent.Future

class CollectionSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var test: ArangoCollection = _

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
      test.create().map { response =>
        response.error should be(false)
      }
    }
    "insert a document" in {
      test.document.create(User("John Doe", 30), waitForSync = true, returnNew = true).map { response =>
        response.`new` should be(Some(User("John Doe", 30)))
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
        response.waitForSync should be(false)
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
        collectionNames should be(Set("test"))
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

case class User(name: String, age: Int)