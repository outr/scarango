package spec

import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango.ArangoDBServer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class ArangoDBSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  "ArangoDB" should {
    lazy val server = ArangoDBServer()
    lazy val db = server.db("scarango_simple")

    "create a database" in {
      db.create().asserting { created =>
        created should be(true)
      }
    }
    "verify the database exists" in {
      db.exists().asserting { exists =>
        exists should be(true)
      }
    }
    "drop a database" in {
      db.drop().asserting { dropped =>
        dropped should be(true)
      }
    }
  }
}
