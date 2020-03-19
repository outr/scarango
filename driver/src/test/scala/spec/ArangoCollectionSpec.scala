package spec

import com.outr.arango.{ArangoDB, ArangoException, DatabaseState, Document, DocumentModel, Id, Index, IndexType, Serialization}
import io.youi.http.Headers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class ArangoCollectionSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "ArangoCollection" should {
    lazy val dbExample = db.api.db("collectionExample")
    lazy val collection = dbExample.collection("test")
    var indexId: Option[Id[Index]] = None
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
      collection.index.create(Index(IndexType.Persistent, List("name"), unique = true, sparse = true)).map { info =>
        indexId = Some(info.id)
        info.error should be(false)
      }
    }
    "insert a document" in {
      collection.document.insertOne(User("John Doe", User.id("john"))).map { insert =>
        insert._id.get._id should be("test/john")
        insert._id.get._key should be("john")
        insert._id.get.collection should be("test")
        insert._id.get.value should be("john")
      }
    }
    "upsert a document" in {
      collection.document.upsertOne(User("Johnny Doe", User.id("john"))).map { upsert =>
        upsert._id.get._id should be("test/john")
        upsert._id.get._key should be("john")
        upsert._id.get.collection should be("test")
        upsert._id.get.value should be("john")
      }
    }
    "query the document back" in {
      collection.document.get[User](User.id("john")).map { user =>
        user should not be None
        user.get.name should be("Johnny Doe")
      }
    }
    "fail to insert a duplicate id" in {
      recoverToSucceededIf[ArangoException] {
        collection.document.insertOne(User("Joe Doe", User.id("john")))
      }
    }
    "fail to insert a duplicate name" in {
      recoverToSucceededIf[ArangoException] {
        collection.document.insertOne(User("Johnny Doe"))
      }
    }
    "delete a document" in {
      collection.document.deleteOne(Id[String]("john", "test")).map { id =>
        id._id should be("test/john")
        id._key should be("john")
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
    "unload the collection" in {
      collection.unload().map { load =>
        load.status should be(2)
      }
    }
    "load the collection" in {
      collection.load().map { load =>
        load.status should be(3)
      }
    }
    "truncate the collection" in {
      collection.truncate().map { response =>
        response.error should be(false)
      }
    }
    "delete the unique index on name" in {
      collection.index.delete(indexId.get).map { delete =>
        delete.error should be(false)
      }
    }
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

  case class User(name: String, _id: Id[User] = User.id()) extends Document[User]

  object User extends DocumentModel[User] {
    override def indexes: List[Index] = Nil

    override val collectionName: String = "users"
    override val serialization: Serialization[User] = Serialization.auto[User]
  }
}