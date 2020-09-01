package spec

import com.outr.arango.{Document, DocumentCollection, DocumentModel, Field, Graph, Id, Index, Serialization}
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class MaterializedSpec extends AsyncWordSpec with Matchers with Eventually {
  "Materialized" should {
    val u1 = User("User 1", 21)

    "initialize configuration" in {
      Profig.initConfiguration().map { _ =>
        succeed
      }
    }
    "initialize the database" in {
      database.init().map { _ =>
        succeed
      }
    }
    "insert a user and verify it exists in materialized" in {
      database.users.insertOne(u1).flatMap { _ =>
        org.scalatest.concurrent.ScalaFutures.whenReady()
      }
    }
  }

  object database extends Graph("materializedSpec") {
    val users: DocumentCollection[User] = vertex[User]
    val locations: DocumentCollection[Location] = vertex[Location]
    val materializedUsers: DocumentCollection[MaterializedUser] = vertex[MaterializedUser]
//    val materializedUsers: DocumentCollection[MaterializedUser] = materialized[MaterializedUser](
//      User,
//      MaterializedUser.name -> User.name,
//      MaterializedUser.age -> User.age,
//      MaterializedUser.locations -> Location.userId
//    )
  }

  case class User(name: String, age: Int, _id: Id[User] = User.id()) extends Document[User]
  object User extends DocumentModel[User] {
    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")

    override val collectionName: String = "users"
    override implicit val serialization: Serialization[User] = Serialization.auto[User]

    override def indexes: List[Index] = Nil
  }

  case class Location(userId: Id[User], city: String, state: String, _id: Id[Location] = Location.id()) extends Document[Location]
  object Location extends DocumentModel[Location] {
    val userId: Field[Id[User]] = field("userId")
    val city: Field[String] = field("city")
    val state: Field[String] = field("state")

    override val collectionName: String = "locations"
    override implicit val serialization: Serialization[Location] = Serialization.auto[Location]

    override def indexes: List[Index] = Nil
  }

  case class MaterializedUser(name: String, age: Int, locations: List[Location], _id: Id[MaterializedUser]) extends Document[MaterializedUser]
  object MaterializedUser extends DocumentModel[MaterializedUser] {
    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")
    val locations: Field[List[Location]] = field("locations")

    override val collectionName: String = "materializedUsers"
    override implicit val serialization: Serialization[MaterializedUser] = Serialization.auto[MaterializedUser]

    override def indexes: List[Index] = Nil
  }
}