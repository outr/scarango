package spec

import com.outr.arango.{ArangoDB, ArangoException, DatabaseState, Document, DocumentModel, Id, IndexType, Serialization}
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
    implicit val serialization: Serialization[User] = User.serialization

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
        info.error should be(false)
      }
    }
    "insert a document" in {
      collection.document.insertOne(User("John Doe", User.id("john"))).map { insert =>
        insert._identity._id should be("test/john")
        insert._identity._key should be("john")
        insert._identity._rev should not be None
        insert._identity.collection should be("test")
        insert._identity.value should be("john")
      }
    }
    "upsert a document" in {
      collection.document.upsertOne(User("Johnny Doe", User.id("john"))).map { upsert =>
        upsert._identity._id should be("test/john")
        upsert._identity._key should be("john")
        upsert._identity._rev should not be None
        upsert._identity.collection should be("test")
        upsert._identity.value should be("john")
      }
    }
    "query the document back" in {
      collection.document.get[User](User.id("john")).map { user =>
        user should not be None
      }
    }
    "fail to insert a duplicate" in {
      recoverToSucceededIf[ArangoException] {
        collection.document.insertOne(User("Joe Doe", User.id("john")))
      }
    }
    "delete a document" in {
      collection.document.deleteOne(Id[String]("john", "test")).map { id =>
        id._id should be("test/john")
        id._key should be("john")
        id._rev should not be None
        id.collection should be("test")
        id.value should be("john")
      }
    }
    "query the document back and get nothing" in {
      collection.document.get[User](User.id("john")).map { user =>
        user should be(None)
      }
    }
    "insert multiple documents" in {
      collection.document.insert(List(User("Jane Doe"), User("Baby Doe"))).map { inserts =>
        inserts.length should be(2)
      }
    }
    // TODO: mess up session token to force a reconnect
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

  case class User(name: String, _identity: Id[User] = User.id()) extends Document[User]

  object User extends DocumentModel[User] {
    override def collectionName: String = "users"

    override def serialization: Serialization[User] = Serialization.auto[User]
  }
}