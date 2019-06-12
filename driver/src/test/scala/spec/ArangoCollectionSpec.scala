package spec

import com.outr.arango.{ArangoDB, DatabaseState, Id, IndexType}
import io.circe.Json
import io.youi.http.Headers
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

class ArangoCollectionSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "ArangoCollection" should {
    lazy val dbExample = db.api.db("collectionExample")
    lazy val collection = dbExample.collection("test")
    var indexId: Option[String] = None

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
      collection.create(waitForSync = Some(true)).map { info =>
        info.name should be(Some("test"))
      }
    }
    "create a unique index on the collection" in {
      collection.index.create(IndexType.Persistent, List("name"), unique = true, sparse = true).map { info =>
        indexId = info.id.map(_.value)
        scribe.info(s"IndexID: $indexId")
        info.error should be(false)
      }
    }
    "insert a document" in {
      collection.document.create(Json.obj(
        "_key" -> Json.fromString("john"),
        "name" -> Json.fromString("John Doe")
      ), returnOld = true).map { response =>
        scribe.info(s"Response: $response")
        succeed
      }
    }
    "upsert a document" in {
      collection.document.create(Json.obj(
        "_key" -> Json.fromString("john"),
        "name" -> Json.fromString("Johnny Doe")
      ), returnOld = true, overwrite = true).map { response =>
        scribe.info(s"Response: $response")
        succeed
      }
    }
    "delete a document" in {
      collection.document.deleteOne(Id[String]("john", "test")).map { json =>
        scribe.info(s"Delete: $json")
        succeed
      }
    }
    "insert multiple documents" in {
      dbExample.collection("test").document.create(Json.arr(
        Json.obj("name" -> Json.fromString("Jane Doe")),
        Json.obj("name" -> Json.fromString("Baby Doe"))
      )).map { response =>
        scribe.info(s"Response: $response")
        succeed
      }
    }
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