package spec

import com.outr.arango.{ArangoDB, DatabaseState}
import io.youi.http.Headers
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

class ArangoCollectionSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "ArangoCollection" should {
    lazy val dbExample = db.api.db("collectionExample")

    "initialize configuration" in {
      Profig.loadDefaults()
      succeed
    }
    "initialize successfully" in {
      db.init().map { state =>
        state shouldBe a[DatabaseState.Initialized]
        db.session.client.request.headers.first(Headers.Request.Authorization) should not be None
      }
    }
    "create a test database" in {
      dbExample.create().map { response =>
        response.value should be(true)
      }
    }
    "verify the database was created" in {
      db.api.db.list().map { response =>
        response.value should contain("collectionExample")
      }
    }
    "create a new collection" in {
      dbExample.collection("test").create(waitForSync = Some(true)).map { info =>
        scribe.info(s"Info: ${info.keyOptions}")
        info.name should be(Some("test"))
      }
    }
    // TODO: create a unique index on the collection
    // TODO: insert a document
    // TODO: mess up session token to force a reconnect
    // TODO: insert another document
    // TODO: check replication state
    // TODO: fail to insert a duplicate document
    // TODO: insert Jane Doe
    // TODO: check replication follow
    // TODO: insert Baby Doe
    // TODO: check replication follow again
    // TODO: get collection info
    // TODO: get collection properties
    // TODO: get collection count
    // TODO: bulk import documents
    // TODO: get collection count after bulk import
    // TODO: get collection revision
    // TODO: list all collections
    // TODO: truncate the collection
    // TODO: delete the unique index on name
    "drop the collection" in {
      dbExample.collection("test").drop().map { success =>
        success should be(true)
      }
    }
    "drop the test database" in {
      dbExample.drop().map { response =>
        response.value should be(true)
      }
    }
    "verify the database dropped" in {
      db.api.db.list().map { response =>
        response.value should not contain "collectionExample"
      }
    }
  }
}