package spec

import com.outr.arango.transaction.{Transaction, TransactionStatus}
import com.outr.arango._
import com.outr.arango.query._
import fabric.rw.{ReaderWriter, ccRW}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

import scala.concurrent.Future

class AdvancedSpec extends AsyncWordSpec with Matchers {
  private var transaction: Transaction = _

  "Advanced" should {
    "initialize configuration" in {
      Profig.initConfiguration()
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
    "create a paged result" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} FILTER p.${Person.name} == 'Adam' RETURN p")
        .paged
        .map { page =>
          page.results.length should be(1)
          page.results.head.name should be("Adam")
        }
    }
    "create a process result" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} FILTER p.${Person.name} == 'Adam' RETURN p")
        .process { response =>
          Future.successful(response.result.map(_.name))
        }
        .map { list =>
          list should be(List(List("Adam")))
        }
    }
    "create an iterate result" in {
      var results = List.empty[String]
      database
        .people
        .query(aql"FOR p IN ${database.people} FILTER p.${Person.name} == 'Adam' RETURN p")
        .iterate { person =>
          results = person.name :: results
          Future.successful(())
        }
        .map { _ =>
          results should be(List("Adam"))
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
      database.people.withTransaction(transaction).insert(List(
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
    "update the records using DSL" in {
      import com.outr.arango.query._

      database
        .people
        .update(Person.age is 21, Person.age(22))
        .map { modified =>
          modified should be(1L)
        }
    }
  }

  object database extends Graph(databaseName = "advanced") {
    val people: DocumentCollection[Person] = vertex[Person]
  }

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    override implicit val rw: ReaderWriter[Person] = ccRW

    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")

    override def indexes: List[Index] = Nil

    override val collectionName: String = "people"
  }
}