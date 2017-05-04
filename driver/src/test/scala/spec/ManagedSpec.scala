package spec

import com.outr.arango.{DocumentOption, Edge, _}
import com.outr.arango.managed._
import io.circe.Decoder
import org.scalatest.{AsyncWordSpec, Matchers}
import io.circe.generic.semiauto._
import io.circe.generic.auto._

import scala.concurrent.Future

class ManagedSpec extends AsyncWordSpec with Matchers {
  var apple: Fruit = _
  var banana: Fruit = _
  var cherry: Fruit = _
  var butterfly: Content = _
  var bunny: Content = _
  var owl: Content = _

  "Managed Graph" should {
    import Database._

    "create the graph" in {
      init().map { b =>
        b should be(true)
      }
    }
    "verify the upgrade ran" in {
      version.map { v =>
        v should be(1)
      }
    }
    "insert Apple" in {
      fruit.insert(Fruit("Apple", Some("Apple"))).map { f =>
        apple = f
        f._id should be(Some("fruit/Apple"))
        f._key should be(Some("Apple"))
        f._rev shouldNot be(None)
        f.name should be("Apple")
      }
    }
    "fail to insert a duplicate named Apple" in {
      fruit.insert(Fruit("Apple")).failed.map {
        case exc: ArangoException => exc.error.errorCode should be(ArangoCode.ArangoUniqueConstraintViolated)
      }
    }
    "insert Banana" in {
      fruit.insert(Fruit("Banana")).map { f =>
        banana = f
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Banana")
      }
    }
    "insert Cherry" in {
      fruit.insert(Fruit("Cherry")).map { f =>
        cherry = f
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Cherry")
      }
    }
    "query Apple back by key" in {
      fruit.apply("Apple").map { f =>
        f.name should be("Apple")
      }
    }
    "query all fruit back" in {
      val query = aql"FOR f IN $fruit RETURN f"
      fruit.cursor(query).map { response =>
        response.error should be(false)
        response.count should be(Some(3))
        response.result.map(_.name).toSet should be(Set("Apple", "Banana", "Cherry"))
      }
    }
    "update Cherry to Mango" in {
      val query = aql"""FOR f IN $fruit FILTER f._key == ${cherry._key.get} UPDATE f._key WITH { name: "Mango" } IN fruit"""
      Database.execute(query).map { success =>
        success should be(true)
      }
    }
    "query Mango back by key" in {
      fruit.apply(cherry._key.get).map { f =>
        f.name should be("Mango")
      }
    }
    "update Mango to Peach using `update`" in {
      Database.fruit.update(cherry._key.get, FruitName("Peach")).map { f =>
        f._key should be(cherry._key.get)
      }
    }
    "query Peach back by key" in {
      fruit.apply(cherry._key.get).map { f =>
        f.name should be("Peach")
      }
    }
    "insert an Image into polymorphic Collection" in {
      content.insert(Image("butterfly", 640, 480)).map { c =>
        butterfly = c
        c.name should be("butterfly")
        c._id shouldNot be(None)
        c._key shouldNot be(None)
        c._rev shouldNot be(None)
      }
    }
    "insert a Video into polymorphic Collection" in {
      content.insert(Video("bunny", 1920, 1080, 60.0)).map { c =>
        bunny = c
        c.name should be("bunny")
        c._id shouldNot be(None)
        c._key shouldNot be(None)
        c._rev shouldNot be(None)
      }
    }
    "insert a Audio into polymorphic Collection" in {
      content.insert(Audio("owl", 15.3)).map { c =>
        owl = c
        c.name should be("owl")
        c._id shouldNot be(None)
        c._key shouldNot be(None)
        c._rev shouldNot be(None)
      }
    }
    "query all Content back" in {
      content.all().map { response =>
        response.total should be(3)
        val map = response.results.map(c => c.name -> c).toMap
        map("butterfly") shouldBe a[Image]
        map("bunny") shouldBe a[Video]
        map("owl") shouldBe a[Audio]
      }
    }
    "create iterator to verify QueryResponsePagination and QueryResponseIterator" in {
      Future {
        val iterator = content.iterator(content.allQuery, batchSize = 1)
        val items = iterator.toVector
        items.map(_.name).toSet should be(Set("butterfly", "bunny", "owl"))
      }
    }
    "create edge for Bunny and Apple" in {
      hasFruit.insert(HasFruit(bunny, apple)).map { e =>
        e._from should be(bunny._id.get)
        e._to should be(apple._id.get)
      }
    }
    "create edge for Bunny and Banana" in {
      hasFruit.insert(HasFruit(bunny, banana)).map { e =>
        e._from should be(bunny._id.get)
        e._to should be(banana._id.get)
      }
    }
    "create edge for Owl and Cherry" in {
      hasFruit.insert(HasFruit(owl, cherry)).map { e =>
        e._from should be(owl._id.get)
        e._to should be(cherry._id.get)
      }
    }
    "query all Fruit for the Bunny" in {
      val query =
        aql"""
             FOR c
             IN $content
             FILTER c._key == ${bunny._key.get}
             LET fruits = (FOR f IN OUTBOUND c._id $hasFruit RETURN f)
             RETURN { content: c, fruit: fruits }
           """
      implicit def contentFruitDecoder: Decoder[ContentFruit] = deriveDecoder[ContentFruit]
      implicit def fruitDecoder: Decoder[Fruit] = fruit.decoder
      implicit def contentDecoder: Decoder[Content] = content.decoder
      cursor[ContentFruit](query, count = true).map { results =>
        results.count should be(Some(1))
        val cf = results.result.head
        cf.content.name should be("bunny")
        cf.fruit.map(_.name).toSet should be(Set("Apple", "Banana"))
      }
    }
    "insert first order" in {
      orders.insert(Order(BigDecimal("100.10"), Status.New, _key = Some("order1"))).map { o =>
        o._id should be(Some("orders/order1"))
      }
    }
    "insert second order" in {
      orders.insert(Order(BigDecimal("12.00"), Status.Paid, _key = Some("order2"))).map { o =>
        o._id should be(Some("orders/order2"))
      }
    }
    "insert third order" in {
      orders.insert(Order(BigDecimal("123.45"), Status.Failure("Bad Credit Card"), _key = Some("order3"))).map { o =>
        o._id should be(Some("orders/order3"))
      }
    }
    "check the first order" in {
      orders.apply("order1").map { o =>
        o.amount should be(BigDecimal("100.10"))
        o.status should be(Status.New)
        o.status shouldNot be(Status.Paid)
        o.status shouldNot be(Status.Failure)
        o.modified shouldNot be(0L)
        o._key should be(Some("order1"))
      }
    }
    "check the second order" in {
      orders.apply("order2").map { o =>
        o.amount should be(BigDecimal("12.00"))
        o.status should be(Status.Paid)
        o.status shouldNot be(Status.New)
        o.status shouldNot be(Status.Failure)
        o.modified shouldNot be(0L)
        o._key should be(Some("order2"))
      }
    }
    "check the third order" in {
      orders.apply("order3").map { o =>
        o.amount should be(BigDecimal("123.45"))
        o.status should be(Status.Failure("Bad Credit Card"))
        o.status shouldNot be(Status.New)
        o.status shouldNot be(Status.Paid)
        o.modified shouldNot be(0L)
        o._key should be(Some("order3"))
      }
    }
    "delete all orders via AQL query" in {
      val query = aql"RETURN LENGTH(FOR o IN $orders REMOVE o IN $orders RETURN o)"
      call[Int](query).map { count =>
        count should be(3)
      }
    }
    "delete the graph" in {
      delete().map { b =>
        b should be(true)
      }
    }
  }

  object Database extends Graph("example") with UpgradeSupport {
    override val store: MapCollection = new MapCollection(this, "store")

    val fruit: VertexCollection[Fruit] = vertex[Fruit]("fruit")
    val content: PolymorphicVertexCollection[Content] = polymorphic3[Content, Image, Video, Audio]("content")
    val hasFruit: EdgeCollection[HasFruit] = edge[HasFruit]("hasFruit", "content" -> "fruit")
    val orders: VertexCollection[Order] = vertex[Order]("orders")

    register(1) {
      fruit.index.persistent.create(List("name"), unique = true, sparse = true).map(_ => ())
    }
  }

  case class Fruit(name: String,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None) extends DocumentOption

  case class FruitName(name: String)

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

  case class Order(amount: BigDecimal,
                   status: Status,
                   modified: Long = 0L,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None) extends DocumentOption with Modifiable

  sealed trait Status

  object Status {
    case object New extends Status
    case object Paid extends Status
    case class Failure(reason: String) extends Status
  }
}