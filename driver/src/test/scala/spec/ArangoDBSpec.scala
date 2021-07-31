package spec

import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango.{ArangoDBServer, CollectionType}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class ArangoDBSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  "ArangoDB" should {
    lazy val server = ArangoDBServer()
    lazy val db = server.db("scarango_simple")
    lazy val coll = db.collection("simple")

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
    // TODO: Create an index
    // TODO: Verify the index
    // TODO: Delete an index
    // TODO: Insert a record
    // TODO: Verify the record
    // TODO: Delete the record
    // TODO: Insert a batch of records
    // TODO: Verify the records
    // TODO: Delete one record
    // TODO: Update a record
    // TODO: Verify the records
    // TODO: Drop a collection
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
