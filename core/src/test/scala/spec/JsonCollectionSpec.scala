package spec

import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.json._
import com.outr.arango.{Graph, Index}
import fabric._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class JsonCollectionSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  "JsonCollection" should {
    "initialize configuration" in {
      Profig.initConfiguration()
      succeed
    }
    "initialize" in {
      database.init().map(_ => succeed)
    }
    "truncate the database" in {
      database.truncate().map(_ => succeed)
    }
    "insert two records" in {
      import UserModel._

      database.users.batch.insert(List(
        obj("name" -> "John Doe"),
        obj("name" -> "Jane Doe")
      )).map(_ => succeed)
    }
    "verify the two records" in {
      database.users.query.toList.map { users =>
        val names = users.map(_.json("name").asString)
        names should be(List("John Doe", "Jane Doe"))
      }
    }
    "dispose" in {
      database.shutdown().map(_ => succeed)
    }
  }

  object database extends Graph("json-collection") {
    val users: DocumentCollection[JsonDocument, JsonDocumentModel] = vertex(UserModel)
  }

  object UserModel extends JsonDocumentModel {
    override val collectionName: String = "users"

    override def indexes: List[Index] = Nil
  }
}