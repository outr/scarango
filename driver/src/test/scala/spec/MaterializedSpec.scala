package spec

import com.outr.arango.api.OperationType
import com.outr.arango._
import com.outr.arango.query._
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class MaterializedSpec extends AsyncWordSpec with Matchers with Eventually {
  "Materialized" should {
    val ec = scribe.Execution.global
    lazy val userMonitor = database.monitor(database.users)
    lazy val locationMonitor = database.monitor(database.locations)

    val u1 = User("User 1", 21)
    val l1 = Location(u1._id, "San Jose", "California")

    def query(ids: NamedRef => Query): Query = {
      val ref = NamedRef("$ids")
      val preQuery = ids(ref)
      val query =
        aqlu"""
              FOR u IN ${database.users}
              FILTER u._id IN $ref
              LET l = (
                FOR loc IN ${database.locations}
                FILTER loc.${Location.userId} IN $ref
                RETURN loc
              )
              INSERT {
                _key: u._key,
                ${MaterializedUser.name}: u.${User.name},
                ${MaterializedUser.age}: u.${User.age},
                ${MaterializedUser.locations}: l
              } INTO ${database.materializedUsers} OPTIONS { overwrite: true }
            """
      preQuery + query
    }

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
    /*"temporary hack to represent materialization" in {
      userMonitor.attach { op =>
        op._key.foreach { userKey =>
          val userId = User.id(userKey)
          if (op.`type` == OperationType.InsertReplaceDocument) {
            val q = query { ref =>
              aqlu"LET $ref = [$userId]"
            }
            database.query(q).update(ec)
          } else if (op.`type` == OperationType.RemoveDocument) {
            database.materializedUsers.deleteOne(MaterializedUser.id(userId.value))
          }
        }
      }
      locationMonitor.attach { op =>
        op._key.foreach { locationKey =>
          val locationId = Location.id(locationKey)
          if (op.`type` == OperationType.InsertReplaceDocument) {
            val q = query { ref =>
              aqlu"LET $ref = [DOCUMENT($locationId).userId]"
            }
            database.query(q).update(ec)
          } else if (op.`type` == OperationType.RemoveDocument) {
            val q = query { ref =>
              aqlu"""
                     LET $ref = (
                       FOR m in ${database.materializedUsers}
                       FILTER $locationId IN m.locations[*]._id
                       RETURN CONCAT('users/', m._key)
                     )
                  """
            }
            database.query(q).update(ec)
          }
        }
      }
      database.monitor.nextTick.flatMap { _ =>
        database.monitor.nextTick.map { _ =>
          succeed
        }
      }
    }*/
    "insert a user and verify it exists in materialized" in {
      database.users.insertOne(u1).flatMap { _ =>
        database.monitor.nextTick.flatMap { _ =>
          database.materializedUsers.all.results.map { list =>
            list should be(List(MaterializedUser(u1.name, u1.age, Nil, MaterializedUser.id(u1._id.value))))
          }
        }
      }
    }
    "insert a location and verify it was added to the materialized" in {
      database.locations.insertOne(l1).flatMap { _ =>
        database.monitor.nextTick.flatMap { _ =>
          database.materializedUsers.all.results.map { list =>
            list should be(List(MaterializedUser(u1.name, u1.age, List(l1), MaterializedUser.id(u1._id.value))))
          }
        }
      }
    }
    "delete a location and verify it was deleted from the materialized" in {
      database.locations.deleteOne(l1._id).flatMap { _ =>
        database.monitor.nextTick.flatMap { _ =>
          database.materializedUsers.all.results.map { list =>
            list should be(List(MaterializedUser(u1.name, u1.age, Nil, MaterializedUser.id(u1._id.value))))
          }
        }
      }
    }
    // TODO: Add another user
    "delete a user and verify it was deleted from materialized" in {
      database.users.deleteOne(u1._id).flatMap { _ =>
        database.monitor.nextTick.flatMap { _ =>
          database.materializedUsers.all.results.map { list =>
            list should be(Nil)
          }
        }
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
    val materializedUsers: DocumentCollection[MaterializedUser] = vertex[MaterializedUser]
    val materialized: MaterializedBuilder[User, MaterializedUser] = users
      .materialized(
        refs => aqlu"""
              FOR u IN ${database.users}
              FILTER u._id IN ${refs.ids}
              LET l = (
                FOR loc IN ${database.locations}
                FILTER loc.${Location.userId} IN ${refs.ids}
                RETURN loc
              )
              LET ${refs.updatedRef} = {
                _key: u._key,
                ${MaterializedUser.name}: u.${User.name},
                ${MaterializedUser.age}: u.${User.age},
                ${MaterializedUser.locations}: l
              }
            """
      )
      .into(materializedUsers)
      .and(locations) { getRefs =>
        aqlu"LET ${getRefs.ids} = [DOCUMENT(${getRefs.dependencyId}).userId]"
      } { getRefs =>
        aqlu"""
               LET ${getRefs.ids} = (
                 FOR m in ${database.materializedUsers}
                 FILTER ${getRefs.dependencyId} IN m.locations[*]._id
                 RETURN CONCAT('users/', m._key)
               )
            """
      }
      .build()
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