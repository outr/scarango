package spec

import com.outr.arango.transaction.Transaction
import com.outr.arango._
import org.scalatest.{Assertion, Matchers}
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class AdvancedSpec extends AsyncWordSpec with Matchers {
  "Advanced" should {
    "initialize configuration" in {
      Profig.loadDefaults()
      succeed
    }
    "initialize" in {
      database.init().map { _ =>
        succeed
      }
    }
    "insert two records" in {
      database.people.insert(List(
        Person("Adam", 21),
        Person("Bethany", 19)
      )).map { _ =>
        succeed
      }
    }
    "verify the two records" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} RETURN p")
        .results
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany"))
        }
    }
    "insert two records in a transaction" in {
      Transaction[Database, Assertion] { db =>
        db.people.insert(List(
          Person("Charles", 35),
          Person("Donna", 41)
        )).map { _ =>
          succeed
        }
      }
    }
    "verify the four records" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} RETURN p")
        .results
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany", "Charles", "Donna"))
        }
    }
  }

  class Database extends Graph(databaseName = "advanced") {
    val people: Collection[Person] = vertex[Person]()
  }
  object database extends Database

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    val name: Field[String] = Field[String]("name")
    val age: Field[Int] = Field[Int]("age")

    override val collectionName: String = "people"
    override implicit val serialization: Serialization[Person] = Serialization.auto[Person]
  }
}