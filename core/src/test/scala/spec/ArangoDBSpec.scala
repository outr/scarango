package spec

import com.outr.arango.{ArangoDB, DatabaseState}
import io.youi.http.Headers
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

class ArangoDBSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "ArangoDB" should {
    lazy val originalPassword = ArangoDB.config.credentials.password
    "initialize configuration" in {
      Profig.loadDefaults()
      originalPassword
      succeed
    }
    "fail to initialize with bad password" in {
      Profig("arango.credentials.password").store("bad")
      val db = new ArangoDB()
      db.init().map { state =>
        state shouldBe a[DatabaseState.Error]
        succeed
      }
    }
    "reset credentials" in {
      Profig("arango.credentials.password").store(originalPassword)
      succeed
    }
    "initialize successfully" in {
      db.init().map { state =>
        state shouldBe a[DatabaseState.Initialized]
        db.session.client.request.headers.first(Headers.Request.Authorization) should not be None
      }
    }
    "get the current database" in {
      db.api.db.current.map { response =>
        response.result.name should be("_system")
      }
    }
    "list the databases" in {
      db.api.db.list().map { response =>
        response.result should contain("_system")
      }
    }
    // TODO: create a test database
    // TODO: verify the database was created
    // TODO: drop the test database
    // TODO: verify the database dropped
  }
}