package spec

import com.outr.arango.ArangoSession
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe.generic.auto._

class CollectionSpec extends AsyncWordSpec with Matchers {
  "Collections" should {
    "create a new collection" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.create().map { response =>
          response.error should be(false)
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "insert a document" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.document.create(User("John Doe", 30), waitForSync = true, returnNew = true).map { response =>
          response.`new` should be(Some(User("John Doe", 30)))
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "get collection information" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.information().map { response =>
          response.name should be("test")
          response.`type` should be(2)
          response.status should be(3)
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "get collection properties" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.properties().map { response =>
          response.waitForSync should be(false)
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "get collection count" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.count().map { response =>
          response.name should be("test")
          response.`type` should be(2)
          response.status should be(3)
          response.count should be(1)
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "get collection revision" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.revision().map { response =>
          response.name should be("test")
          response.`type` should be(2)
          response.status should be(3)
          response.revision shouldNot be("0")
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "list all collections" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.list().map { response =>
          val collectionNames = response.result.map(_.name).toSet
          collectionNames should be(Set("test"))
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "truncate the collection" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.truncate().map { response =>
          response.error should be(false)
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
    "drop the new collection" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val collection = dbSession.collection("test")
        val future = collection.drop().map { response =>
          response.error should be(false)
        }
        future.onComplete { _ =>
          session.instance.dispose()
        }
        future
      }
    }
  }
}

case class User(name: String, age: Int)