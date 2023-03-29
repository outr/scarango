package spec

import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query.sc2AQL
import com.outr.arango.{Document, DocumentModel, Field, Graph, Id, Index}
import fabric.rw.RW
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class StructuredSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  "Structured" should {
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
    "verify the parent fields are properly set" in {
      User.name.parent should be(None)
      User.addresses.parent should be(None)
      User.addresses.lines.parent should be(Some(User.addresses))
    }
    "insert several users in the database" in {
      database.users.batch.insert(List(
        User("Ann", List(Address(List("One", "Two", "Three")))),
        User("Bob", List(Address(List("Two", "Three", "Four")))),
        User("Cindy", List(Address(List("Three", "Four", "Five")))),
        User("Don", List(Address(List("Four", "Five", "Six")))),
        User("Erin", List(Address(List("Five", "Six", "Seven"))))
      )).map(_ => succeed)
    }
    "query by filter" in {
      val query = database.users.query
        .byFilter(User.addresses.lines is "One")
      query
        .all
        .map { users =>
          users.map(_.name) should be(List("Ann"))
        }
    }
    "query with AQL" in {
      val query =
        aql"""
            FOR u IN ${database.users}
            FILTER "Two" IN u.${User.addresses.lines}
            RETURN u
           """
      database.users.query(query)
        .all
        .map { users =>
          users.map(_.name) should be(List("Ann", "Bob"))
        }
    }
    "dispose the database" in {
      database.shutdown().map(_ => succeed)
    }
  }

  object database extends Graph("structured") {
    val users: DocumentCollection[User] = vertex[User](User)
  }

  case class User(name: String, addresses: List[Address], _id: Id[User] = User.id()) extends Document[User]

  object User extends DocumentModel[User] {
    override implicit val rw: RW[User] = RW.gen

    val name: Field[String] = field("name")
    object addresses extends Field[List[Address]]("addresses", isArray = true) {
      val lines: Field[List[String]] = field("lines", isArray = true)
    }

    override val collectionName: String = "users"

    override def indexes: List[Index] = List(
      name.index.persistent(), addresses.lines.index.persistent()
    )
  }

  case class Address(lines: List[String])

  object Address {
    implicit val rw: RW[Address] = RW.gen
  }
}