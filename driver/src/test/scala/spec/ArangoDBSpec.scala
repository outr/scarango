package spec

import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango.{ArangoDBServer, CollectionType, CreateOptions, DeleteOptions, Query}
import fabric._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class ArangoDBSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  "ArangoDB" should {
    lazy val server = ArangoDBServer()
    lazy val db = server.db("scarango_simple")
    lazy val coll = db.collection("simple")
    lazy val nameField = coll.field[String]("name")

    var nameFieldId: String = ""
    var johnDoeKey: String = ""

    "verify the database does not exist" in {
      db.exists().asserting { exists =>
        exists should be(false)
      }
    }
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
    "verify the collection doesn't already exist" in {
      coll.exists().asserting { exists =>
        exists should be(false)
      }
    }
    "create a collection" in {
      coll.create().asserting { info =>
        info.`type` should be(CollectionType.Document)
        info.name should be("simple")
      }
    }
    "verify the collection exists" in {
      coll.exists().asserting { exists =>
        exists should be(true)
      }
    }
    "get the collection info" in {
      coll.info().asserting { info =>
        info.`type` should be(CollectionType.Document)
        info.name should be("simple")
      }
    }
    "create an index" in {
      coll.index.ensure(List(nameField.index.persistent())).asserting { list =>
        list.size should be(1)
        val info = list.head
        nameFieldId = info.id
        info.`type` should be("persistent")
        info.fields should be(Some(List("name")))
      }
    }
    "verify the index was created" in {
      coll.index.query().asserting { list =>
        list.size should be(2)
        val info = list.find(_.id == nameFieldId).getOrElse(fail())
        info.`type` should be("persistent")
        info.fields should be(Some(List("name")))
      }
    }
    "insert a document" in {
      coll.document.insert(obj(
        "name" -> "John Doe",
        "age" -> 21
      ), CreateOptions(waitForSync = true, returnNew = true, silent = false)).asserting { result =>
        result.newDocument.flatMap(_.get("name").map(_.asString)) should be(Some("John Doe"))
        johnDoeKey = result.newDocument.get("_key").asString
        result.newDocument.flatMap(_.get("age").map(_.asInt)) should be(Some(21))
      }
    }
    "verify the document" in {
      db.query(Query("FOR s IN simple RETURN s")).compile.toList.asserting { results =>
        results.map(_.get("name").map(_.asString)) should be(List(Some("John Doe")))
      }
    }
    "delete the document" in {
      coll.document.delete(johnDoeKey, DeleteOptions(returnOld = true, silent = false)).asserting { result =>
        result.key should be(Some(johnDoeKey))
        result.oldDocument.flatMap(_.get("name").map(_.asString)) should be(Some("John Doe"))
      }
    }
    // TODO: Insert a batch of records
    // TODO: Verify the records
    // TODO: Verify querying documents with arguments
    // TODO: Delete one record
    // TODO: Update a record
    // TODO: Verify the records
    "delete the index" in {
      coll.index.delete(List(nameFieldId)).asserting { list =>
        list should be(List(nameFieldId))
      }
    }
    "verify the index was deleted" in {
      coll.index.query().asserting { list =>
        list.size should be(1)
      }
    }
    "drop a collection" in {
      coll.drop()
    }
    "verify the collection has been dropped" in {
      coll.exists().asserting { exists =>
        exists should be(false)
      }
    }
    "drop a database" in {
      db.drop().asserting { dropped =>
        dropped should be(true)
      }
    }
    "verify the database no longer exists" in {
      db.exists().asserting { exists =>
        exists should be(false)
      }
    }
  }
}
