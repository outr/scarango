package spec

import com.outr.arango.{ArangoDB, ArangoSession}
import org.scalatest.{AsyncWordSpec, Matchers}

class DatabaseSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: Option[ArangoDB] = None

  "Database" should {
    "create the session" in {
      ArangoSession.default.map { s =>
        session = s
        s.token shouldNot be("")
      }
    }
    "get the current database" in {
      session.db.current.map { info =>
        info.error should be(false)
        info.code should be(200)
        info.result.id should be("1")
        info.result.isSystem should be(true)
        info.result.name should be("_system")
      }
    }
    "list the databases" in {
      session.db.list().map { result =>
        result.error should be(false)
        result.code should be(200)
        result.result should be(List("_system"))
      }
    }
    "create a test database" in {
      session.db("databaseExample").create().map { result =>
        result.error should be(false)
        result.code should be(201)
        result.result should be(true)
      }
    }
    "verify the database was created" in {
      session.db.list().map { result =>
        result.error should be(false)
        result.code should be(200)
        result.result.toSet should be(Set("_system", "databaseExample"))
      }
    }
    "drop the test database" in {
      session.db("databaseExample").drop().map { result =>
        result.error should be(false)
        result.code should be(200)
        result.result should be(true)
      }
    }
    "verify the database dropped" in {
      session.db.list().map { result =>
        result.error should be(false)
        result.code should be(200)
        result.result should be(List("_system"))
      }
    }
  }
}
