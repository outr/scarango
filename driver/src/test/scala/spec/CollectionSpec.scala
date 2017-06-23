package spec

import com.outr.arango.rest.EventType
import com.outr.arango.{ArangoCode, ArangoCollection, ArangoDB, ArangoException, ArangoSession}
import io.circe.Encoder
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveEncoder

import scala.concurrent.Future

class CollectionSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var test: ArangoCollection = _

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  private var nameIndexId: Option[String] = None
  private var lastLogTick: Long = _

  private val isTravis = Option(System.getenv("TRAVIS")).getOrElse("false").toBoolean

  "Collections" should {
    "create the session" in {
      ArangoSession.default.map { s =>
        session = s
        db = session.db()
        test = db.collection("test")
        s.token shouldNot be("")
      }
    }
    "create a new collection" in {
      test.create(waitForSync = true).map { response =>
        response.error should be(false)
      }
    }
    "create a unique index on `name`" in {
      test.index.persistent.create(List("name"), unique = true, sparse = true).map { response =>
        nameIndexId = response.id.map(s => s.substring(s.indexOf('/') + 1))
        response.error should be(false)
      }
    }
    "insert a document" in {
      test.document.create(User("John Doe", 30), returnNew = true).map { response =>
        response.`new` shouldNot be(None)
        val user = response.`new`.head
        user.name should be("John Doe")
        user.age should be(30)
        user._id shouldNot be(None)
        user._key shouldNot be(None)
        user._rev shouldNot be(None)
      }
    }
    "mess up the session token to force a reconnect" in {
      session.token = Some("broken")
      Future.successful(session.token should be(Some("broken")))
    }
    "insert another document causing a reconnect" in {
      test.document.create(User("John Smith", 123), returnNew = true).map { response =>
        response.`new` shouldNot be(None)
        val user = response.`new`.head
        user.name should be("John Smith")
        user.age should be(123)
        user._id shouldNot be(None)
        user._key shouldNot be(None)
        user._rev shouldNot be(None)
      }
    }
    "check replication state" in {
      db.replication.state().map { state =>
        state.state.lastLogTick shouldNot be("")
        lastLogTick = state.state.lastLogTick
        state.state.running should be(true)
      }
    }
    "fail to insert a duplicate named user" in {
      test.document.create(User("John Doe", 30), returnNew = true).failed.map {
        case exc: ArangoException => exc.error.errorCode should be(ArangoCode.ArangoUniqueConstraintViolated)
      }
    }
    "insert Jane Doe" in {
      test.document.create(User("Jane Doe", 28), returnNew = true).map { response =>
        response.`new` shouldNot be(None)
        val user = response.`new`.head
        user.name should be("Jane Doe")
        user.age should be(28)
        user._id shouldNot be(None)
        user._key shouldNot be(None)
        user._rev shouldNot be(None)
      }
    }
    if (!isTravis) {    // TODO: remove this once replication is working on Travis
      "check replication follow" in {
        db.replication.follow(from = Some(lastLogTick)).map { follow =>
          follow.active should be(true)
          follow.checkMore should be(false)
          follow.events.length should be(1)
          val event = follow.events.head
          event.eventType should be(EventType.DocumentUpsert)
          event.collection should be("test")
          event.data shouldNot be(None)
        }
      }
    }
    "insert Baby Doe" in {
      test.document.create(User("Baby Doe", 1), returnNew = true).map { response =>
        response.`new` shouldNot be(None)
        val user = response.`new`.head
        user.name should be("Baby Doe")
        user.age should be(1)
        user._id shouldNot be(None)
        user._key shouldNot be(None)
        user._rev shouldNot be(None)
      }
    }
    if (!isTravis) {
      "check replication follow again" in {
        db.replication.follow(from = Some(lastLogTick)).map { follow =>
          follow.active should be(true)
          follow.checkMore should be(false)
          follow.events.length should be(2)
        }
      }
    }
    "get collection information" in {
      test.information().map { response =>
        response.name should be("test")
        response.`type` should be(2)
        response.status should be(3)
      }
    }
    "get collection properties" in {
      test.properties().map { response =>
        response.waitForSync should be(true)
      }
    }
    "get collection count" in {
      test.count().map { response =>
        response.name should be("test")
        response.`type` should be(2)
        response.status should be(3)
        response.count should be(4)
      }
    }
    "get collection revision" in {
      test.revision().map { response =>
        response.name should be("test")
        response.`type` should be(2)
        response.status should be(3)
        response.revision shouldNot be("0")
      }
    }
    "list all collections" in {
      test.list().map { response =>
        val collectionNames = response.result.map(_.name).toSet
        collectionNames should contain("test")
      }
    }
    "truncate the collection" in {
      test.truncate().map { response =>
        response.error should be(false)
      }
    }
    "delete the unique index on `name`" in {
      test.index.delete(nameIndexId.get).map { response =>
        response.error should be(false)
      }
    }
    "drop the new collection" in {
      test.drop().map { response =>
        response.error should be(false)
      }
    }
    "dispose the session" in {
      Future {
        session.instance.dispose()
        session.instance.isDisposed should be(true)
      }
    }
  }
}

case class User(name: String,
                age: Int,
                _key: Option[String] = None,
                _id: Option[String] = None,
                _rev: Option[String] = None)