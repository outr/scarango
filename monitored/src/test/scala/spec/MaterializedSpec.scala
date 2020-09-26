package spec

import com.outr.arango._
import com.outr.arango.monitored.{Materialized, MaterializedPart, MonitoredSupport, QueryInfo, Reference}
import com.outr.arango.query.AQLInterpolator
import org.scalatest.Assertion
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig
import reactify._

class MaterializedSpec extends AsyncWordSpec with Matchers with Eventually {
  "Materialized" should {
    val u1 = User("User 1", 21)
    val u2 = User("User 2", 31)
    val l1 = Location(u1._id, "San Jose", "California")
    val l2 = Location(u2._id, "Hollywood", "California")
    val l3 = Location(u2._id, "Santa Cruz", "California")

    val fu1Empty = FullUser(u1.name, u1.age, Nil, 0, FullUser.id(u1._id.value))
    val fu1Location = fu1Empty.copy(locations = List(l1), locationCount = 1)
    val fu2Empty = FullUser(u2.name, u2.age, Nil, 0, FullUser.id(u2._id.value))
    val fu2Location = fu2Empty.copy(locations = List(l2), locationCount = 1)
    val fu2Locations = fu2Empty.copy(locations = List(l2, l3), locationCount = 2)
    val fu2LocationsUpdated = fu2Locations.copy(age = 41)

    var updated: Var[List[Id[FullUser]]] = Var(Nil)
    var deleted: Var[List[Id[FullUser]]] = Var(Nil)

    @inline
    def verifyUpdatedAndClear(ids: Id[User]*): Assertion = try {
      updated() should be(ids.toList.map(id => Id[FullUser](id.value, database.fullUsers.name)))
    } finally {
      updated @= Nil
    }

    @inline
    def verifyDeletedAndClear(ids: Id[User]*): Assertion = try {
      deleted() should be(ids.toList.map(id => Id[FullUser](id.value, database.fullUsers.name)))
    } finally {
      deleted @= Nil
    }

    "initialize configuration" in {
      Profig.initConfiguration().map { _ =>
        succeed
      }
    }
    "initialize the database" in {
      database.init().map { _ =>
        database.materialized.updated.attach { id =>
          updated @= updated ::: List(id)
        }
        database.materialized.deleted.attach { id =>
          deleted @= deleted ::: List(id)
        }

        succeed
      }
    }
    "insert a user and verify it exists in materialized" in {
      database.users.insertOne(u1).flatMap { _ =>
        database.monitor.nextTick.flatMap { _ =>
          database.fullUsers.all.results.map { list =>
            list should be(List(fu1Empty))
            verifyUpdatedAndClear(u1._id)
            verifyDeletedAndClear()
          }
        }
      }
    }
    "insert a location and verify it was added to the materialized" in {
      val changedFuture = updated.future()
      for {
        _ <- database.locations.insertOne(l1)
        _ <- changedFuture
        list <- database.fullUsers.all.results
      } yield {
        list should be(List(fu1Location))
        verifyUpdatedAndClear(u1._id)
        verifyDeletedAndClear()
      }
    }
    "insert another user and verify it exists in materialized" in {
      database.users.insertOne(u2).flatMap { _ =>
        database.monitor.nextTick.flatMap { _ =>
          database.fullUsers.all.results.map { list =>
            list.map(_.name).sorted should be(List("User 1", "User 2"))
            verifyUpdatedAndClear(u2._id)
            verifyDeletedAndClear()
          }
        }
      }
    }
    "delete a location and verify it was deleted from the materialized" in {
      val changedFuture = updated.future()
      for {
        _ <- database.locations.deleteOne(l1._id)
        _ <- changedFuture
        list <- database.fullUsers.all.results
      } yield {
        list.toSet should be(Set(fu1Empty, fu2Empty))
        verifyUpdatedAndClear(u1._id)
        verifyDeletedAndClear()
      }
    }
    "delete a user and verify it was deleted from materialized" in {
      val changedFuture = deleted.future()
      for {
        _ <- database.users.deleteOne(u1._id)
        _ <- changedFuture
        list <- database.fullUsers.all.results
      } yield {
        list should be(List(fu2Empty))
        verifyUpdatedAndClear()
        verifyDeletedAndClear(u1._id)
      }
    }
    "insert a location for user 2 and verify it was added to the materialized" in {
      val changedFuture = updated.future()
      for {
        _ <- database.locations.insertOne(l2)
        _ <- changedFuture
        list <- database.fullUsers.all.results
      } yield {
        list should be(List(fu2Location))
        verifyUpdatedAndClear(u2._id)
        verifyDeletedAndClear()
      }
    }
    "insert a second location for user 2 and verify it was added to the materialized" in {
      val changedFuture = updated.future()
      for {
        _ <- database.locations.insertOne(l3)
        _ <- changedFuture
        list <- database.fullUsers.all.results
      } yield {
        list should be(List(fu2Locations))
        verifyUpdatedAndClear(u2._id)
        verifyDeletedAndClear()
      }
    }
    "issue an update to all users and verify it applies" in {
      val changedFuture = updated.future()
      for {
        _ <- database.users.updateAll(FieldAndValue(User.age, 41))
        _ <- changedFuture
        list <- database.fullUsers.all.results
      } yield {
        list should be(List(fu2LocationsUpdated))
        verifyUpdatedAndClear(u2._id)
        verifyDeletedAndClear()
      }
    }
    "truncate materialized and verify empty" in {
      for {
        _ <- database.fullUsers.truncate()
        list <- database.fullUsers.all.results
      } yield {
        list should be(Nil)
      }
    }
    "execution refreshAll to rebuild the materialized view" in {
      for {
        _ <- database.materialized.refreshAll()
        list <- database.fullUsers.all.results
      } yield {
        list should be(List(fu2LocationsUpdated))
      }
    }
    "cleanup" in {
      for {
        _ <- database.monitor.stop()
        _ <- database.drop()
      } yield {
        succeed
      }
    }
  }

  object database extends Graph("materializedSpec") with MonitoredSupport {
    val users: DocumentCollection[User] = vertex[User]
    val locations: DocumentCollection[Location] = vertex[Location]
    val fullUsers: DocumentCollection[FullUser] = vertex[FullUser]

    val materialized: Materialized[User, FullUser] = materialized(users -> fullUsers)
      .map(User.name -> FullUser.name)
      .map(User.age -> FullUser.age)
      .one2Many(locations, Location.userId, FullUser.locations)
      .withField(FullUser.locationCount) { info =>
        val collectionRef = NamedRef()
        aqlu"""
               FIRST(
                 FOR $collectionRef IN ${database.locations}
                 FILTER $collectionRef.${Location.userId} == ${info.baseRef}._id
                 COLLECT WITH COUNT INTO length
                 RETURN length
               )
            """
      }
      .build()
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

  case class FullUser(name: String, age: Int, locations: List[Location], locationCount: Int, _id: Id[FullUser]) extends Document[FullUser]
  object FullUser extends DocumentModel[FullUser] {
    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")
    val locations: Field[List[Location]] = field("locations")
    val locationCount: Field[Int] = field("locationCount")

    override val collectionName: String = "materializedUsers"
    override implicit val serialization: Serialization[FullUser] = Serialization.auto[FullUser]

    override def indexes: List[Index] = Nil
  }
}