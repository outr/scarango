package spec

import com.outr.arango.api.OperationType
import com.outr.arango.{ArangoDB, Credentials, DatabaseState}
import io.youi.http.Headers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class ArangoDatabaseSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "ArangoDatabase" should {
    "initialize configuration" in {
      Profig.loadDefaults()
      succeed
    }
    "fail to initialize with bad password" in {
      val db = new ArangoDB(credentials = Some(Credentials("root", "bad")))
      db.init().map { state =>
        state shouldBe a[DatabaseState.Error]
        succeed
      }
    }
    "initialize successfully" in {
      db.init().map { state =>
        state shouldBe a[DatabaseState.Initialized]
        db.session.client.request.headers.first(Headers.Request.Authorization) should not be None
      }
    }
    "get the current database" in {
      db.api.db.current.map { response =>
        response.value.name should be("_system")
      }
    }
    "list the databases" in {
      db.api.db.list().map { response =>
        response.value should contain("_system")
      }
    }
    "create a test database" in {
      db.api.db("databaseExample").create().map { response =>
        response.value should be(true)
      }
    }
    "verify the database was created" in {
      db.api.db.list().map { response =>
        response.value should contain("databaseExample")
      }
    }
    "check the WAL" in {
      db.api.db("databaseExample").wal.tail().map { ops =>
        ops.operations.length should be(1)
        val op = ops.operations.head
        op.`type` should be(OperationType.CreatedDatabase)
        op.db should be("databaseExample")
      }
    }
    "drop the test database" in {
      db.api.db("databaseExample").drop().map { response =>
        response.value should be(true)
      }
    }
    "verify the database dropped" in {
      db.api.db.list().map { response =>
        response.value should not contain "databaseExample"
      }
    }
  }
}