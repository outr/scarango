package spec

import com.outr.arango.ArangoSession
import org.scalatest.{AsyncWordSpec, Matchers}

class CollectionSpec extends AsyncWordSpec with Matchers {
  "Collections" should {
    "create a new collection" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.createCollection("test").map { response =>
          response.error should be(false)
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "get collection information" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.collectionInformation("test").map { response =>
          response.name should be("test")
          response.`type` should be(2)
          response.status should be(3)
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "get collection properties" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.collectionProperties("test").map { response =>
          response.waitForSync should be(false)
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "get collection count" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.collectionCount("test").map { response =>
          response.name should be("test")
          response.`type` should be(2)
          response.status should be(3)
          response.count should be(0)
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "get collection revision" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.collectionRevision("test").map { response =>
          response.name should be("test")
          response.`type` should be(2)
          response.status should be(3)
          response.revision should be("0")
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "list all collections" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.collections().map { response =>
          val collectionNames = response.result.map(_.name).toSet
          collectionNames should be(Set("test"))
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "truncate the collection" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.truncateCollection("test").map { response =>
          response.error should be(false)
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
    "drop the new collection" in {
      ArangoSession.default.flatMap { session =>
        val dbSession = session.db("_system")
        val future = dbSession.dropCollection("test").map { response =>
          response.error should be(false)
        }
        future.onComplete { _ =>
          session.server.dispose()
        }
        future
      }
    }
  }
}