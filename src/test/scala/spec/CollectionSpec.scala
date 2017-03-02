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