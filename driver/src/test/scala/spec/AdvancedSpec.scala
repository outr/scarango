package spec

import com.outr.arango.transaction.{Transaction, TransactionStatus}
import com.outr.arango._
import org.scalatest.{Assertion, Matchers}
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class AdvancedSpec extends AsyncWordSpec with Matchers {
  private var transaction: Transaction = _

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
    "truncate the database" in {
      database.truncate().map { _ =>
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
    "create a transaction" in {
      database.transaction().map { t =>
        transaction = t
        t.status should be(TransactionStatus.Running)
      }
    }
    "check the status of the transaction" in {
      transaction.checkStatus().map { t =>
        t.status should be(TransactionStatus.Running)
      }
    }
    "abort the transaction" in {
      transaction.abort().map { _ =>
        succeed
      }
    }
    "create a second transaction" in {
      database.transaction(write = List(database.people)).map { t =>
        transaction = t
        t.status should be(TransactionStatus.Running)
      }
    }
    "insert two records in a transaction" in {
      database.people(transaction).insert(List(
        Person("Charles", 35),
        Person("Donna", 41)
      )).map { _ =>
        succeed
      }
    }
    "verify the records aren't visible before commit" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} RETURN p")
        .results
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany"))
        }
    }
    "commit the transaction" in {
      transaction.commit().map { _ =>
        succeed
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
    /*"update the records using DSL" in {
      import com.outr.arango.aql._

      database
        .people
        .update(Person.age is 21, Person.age(22))
        .map { results =>
          results.length should be(1)
          val adam = results.head
          adam.name should be("Adam")
          adam.age should be(22)
        }
    }*/
  }

  object database extends Graph(databaseName = "advanced") {
    val people: Collection[Person] = vertex[Person]
  }

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    val name: Field[String] = Field[String]("name")
    val age: Field[Int] = Field[Int]("age")

    override def indexes: List[Index] = Nil

    override val collectionName: String = "people"
    override implicit val serialization: Serialization[Person] = Serialization.auto[Person]
  }
}