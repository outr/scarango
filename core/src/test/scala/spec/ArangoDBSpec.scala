package spec

import com.outr.arango.{ArangoDB, DatabaseState}
import io.youi.http.Headers
import org.scalatest.{AsyncWordSpec, Matchers}

class ArangoDBSpec extends AsyncWordSpec with Matchers {
  private val db = new ArangoDB()

  "ArangoDB" should {
    "initialize successfully" in {
      db.init().map { state =>
        state shouldBe a[DatabaseState.Initialized]
        db.session.client.request.headers.first(Headers.Request.Authorization) should not be None
      }
    }
  }
}