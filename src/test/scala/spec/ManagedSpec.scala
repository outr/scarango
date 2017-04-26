package spec

import com.outr.arango._
import com.outr.arango.managed.{Collection, Graph}
import com.outr.arango.rest.{CreateInfo, VertexInsert}
import com.outr.arango.{ArangoDB, ArangoGraph, ArangoSession, DocumentOption}
import io.circe.{Decoder, Encoder}
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe.generic.semiauto._

class ManagedSpec extends AsyncWordSpec with Matchers {
  "Managed Graph" should {
    "create the graph" in {
      ExampleGraph.init().map { b =>
        b should be(true)
      }
    }
    "create the Fruit collection" in {
      Fruit.create().map { response =>
        response.error should be(false)
      }
    }
    "insert Apple" in {
      Fruit.insert(Fruit("Apple", Some("Apple"))).map { f =>
        f._id should be(Some("fruit/Apple"))
        f._key should be(Some("Apple"))
        f._rev shouldNot be(None)
        f.name should be("Apple")
      }
    }
    "insert Banana" in {
      Fruit.insert(Fruit("Banana")).map { f =>
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Banana")
      }
    }
    "insert Cherry" in {
      Fruit.insert(Fruit("Cherry")).map { f =>
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Cherry")
      }
    }
    "query Apple back by key" in {
      Fruit.byKey("Apple").map { f =>
        f.name should be("Apple")
      }
    }
    "query all fruit back" in {
      val query = aql"FOR f IN fruit RETURN f"
      Fruit.cursor(query).map { response =>
        response.error should be(false)
        response.count should be(Some(3))
        response.result.map(_.name).toSet should be(Set("Apple", "Banana", "Cherry"))
      }
    }
    "delete the graph" in {
      ExampleGraph.delete().map { b =>
        b should be(true)
      }
    }
  }

  object ExampleGraph extends Graph("example")

  case class Fruit(name: String,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None) extends DocumentOption

  // TODO: make the following generated from Macro
  object Fruit extends Collection[Fruit](ExampleGraph, "fruit") {
    override protected implicit val encoder: Encoder[Fruit] = deriveEncoder[Fruit]
    override protected implicit val decoder: Decoder[Fruit] = deriveDecoder[Fruit]
    override protected def updateDocument(document: Fruit, info: CreateInfo): Fruit = {
      document.copy(_key = Option(info._key), _id = Option(info._id), _rev = Option(info._rev))
    }
  }
}