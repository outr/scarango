package spec

import com.outr.arango._
import com.outr.arango.managed._
import com.outr.arango.DocumentOption
import com.outr.arango.rest.Edge
import io.circe.Decoder
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe.generic.semiauto._

class ManagedSpec extends AsyncWordSpec with Matchers {
  var apple: Fruit = _
  var banana: Fruit = _
  var cherry: Fruit = _
  var butterfly: Content = _
  var bunny: Content = _
  var owl: Content = _

  "Managed Graph" should {
    "create the graph" in {
      ExampleGraph.init().map { b =>
        b should be(true)
      }
    }
    "create the Fruit collection" in {
      ExampleGraph.fruit.create().map { response =>
        response.error should be(false)
      }
    }
    "insert Apple" in {
      ExampleGraph.fruit.insert(Fruit("Apple", Some("Apple"))).map { f =>
        apple = f
        f._id should be(Some("fruit/Apple"))
        f._key should be(Some("Apple"))
        f._rev shouldNot be(None)
        f.name should be("Apple")
      }
    }
    "insert Banana" in {
      ExampleGraph.fruit.insert(Fruit("Banana")).map { f =>
        banana = f
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Banana")
      }
    }
    "insert Cherry" in {
      ExampleGraph.fruit.insert(Fruit("Cherry")).map { f =>
        cherry = f
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Cherry")
      }
    }
    "query Apple back by key" in {
      ExampleGraph.fruit.byKey("Apple").map { f =>
        f.name should be("Apple")
      }
    }
    "query all fruit back" in {
      val query = aql"FOR f IN fruit RETURN f"
      ExampleGraph.fruit.cursor(query).map { response =>
        response.error should be(false)
        response.count should be(Some(3))
        response.result.map(_.name).toSet should be(Set("Apple", "Banana", "Cherry"))
      }
    }
    "create the Content collection" in {
      ExampleGraph.content.create().map { response =>
        response.error should be(false)
      }
    }
    "insert an Image into polymorphic Collection" in {
      ExampleGraph.content.insert(Image("butterfly", 640, 480)).map { c =>
        butterfly = c
        c.name should be("butterfly")
        c._id shouldNot be(None)
        c._key shouldNot be(None)
        c._rev shouldNot be(None)
      }
    }
    "insert a Video into polymorphic Collection" in {
      ExampleGraph.content.insert(Video("bunny", 1920, 1080, 60.0)).map { c =>
        bunny = c
        c.name should be("bunny")
        c._id shouldNot be(None)
        c._key shouldNot be(None)
        c._rev shouldNot be(None)
      }
    }
    "insert a Audio into polymorphic Collection" in {
      ExampleGraph.content.insert(Audio("owl", 15.3)).map { c =>
        owl = c
        c.name should be("owl")
        c._id shouldNot be(None)
        c._key shouldNot be(None)
        c._rev shouldNot be(None)
      }
    }
    "query all Content back" in {
      ExampleGraph.content.all().map { response =>
        response.error should be(false)
        response.count should be(Some(3))
        val map = response.result.map(c => c.name -> c).toMap
        map("butterfly") shouldBe a[Image]
        map("bunny") shouldBe a[Video]
        map("owl") shouldBe a[Audio]
      }
    }
    "create the HasFruit edge collection" in {
      ExampleGraph.hasFruit.create().map { response =>
        response.error should be(false)
      }
    }
    "create edge for Bunny and Apple" in {
      ExampleGraph.hasFruit.insert(HasFruit(bunny, apple)).map { e =>
        e._from should be(bunny._id.get)
        e._to should be(apple._id.get)
      }
    }
    "create edge for Bunny and Banana" in {
      ExampleGraph.hasFruit.insert(HasFruit(bunny, banana)).map { e =>
        e._from should be(bunny._id.get)
        e._to should be(banana._id.get)
      }
    }
    "create edge for Owl and Cherry" in {
      ExampleGraph.hasFruit.insert(HasFruit(owl, cherry)).map { e =>
        e._from should be(owl._id.get)
        e._to should be(cherry._id.get)
      }
    }
    "query all Fruit for the Bunny" in {
      val query =
        aql"""
             FOR c
             IN content
             FILTER c._key == ${bunny._key.get}
             LET fruits = (FOR f IN OUTBOUND c._id hasFruit RETURN f)
             RETURN { content: c, fruit: fruits }
           """
      implicit def contentFruitDecoder: Decoder[ContentFruit] = deriveDecoder[ContentFruit]
      implicit def fruitDecoder: Decoder[Fruit] = ExampleGraph.fruit.decoder
      implicit def contentDecoder: Decoder[Content] = ExampleGraph.content.decoder
      ExampleGraph.cursor[ContentFruit](query, count = true).map { results =>
        results.count should be(Some(1))
        val cf = results.result.head
        cf.content.name should be("bunny")
        cf.fruit.map(_.name).toSet should be(Set("Apple", "Banana"))
      }
    }
    "delete the graph" in {
      ExampleGraph.delete().map { b =>
        b should be(true)
      }
    }
  }

  object ExampleGraph extends Graph("example") {
    val fruit: VertexCollection[Fruit] = vertex[Fruit]("fruit")
    val content: PolymorphicVertexCollection[Content] = polymorphic3[Content, Image, Video, Audio]("content")
    val hasFruit: EdgeCollection[HasFruit] = edge[HasFruit]("hasFruit", "content" -> "fruit")
  }

  case class Fruit(name: String,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None) extends DocumentOption

  trait Content extends PolymorphicDocumentOption {
    def name: String
  }

  case class Image(name: String,
                   width: Int,
                   height: Int,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None,
                   _type: String = "image") extends Content

  case class Video(name: String,
                   width: Int,
                   height: Int,
                   length: Double,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None,
                   _type: String = "video") extends Content

  case class Audio(name: String,
                   length: Double,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None,
                   _type: String = "audio") extends Content

  case class HasFruit(_from: String,
                      _to: String,
                      `type`: String,
                      _key: Option[String],
                      _id: Option[String],
                      _rev: Option[String]) extends Edge with DocumentOption

  object HasFruit {
    def apply(contentKey: String, fruitKey: String, `type`: String = ""): HasFruit = HasFruit(
      _from = s"content/$contentKey",
      _to = s"fruit/$fruitKey",
      `type` = `type`,
      _key = None,
      _id = None,
      _rev = None
    )

    def apply(content: Content, fruit: Fruit): HasFruit = apply(content._key.get, fruit._key.get)
  }

  case class ContentFruit(content: Content, fruit: List[Fruit])
}