package spec

import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core.{StreamTransaction, TransactionLock, TransactionStatus}
import com.outr.arango.query._
import com.outr.arango.query.dsl._
import fabric.rw.{ReaderWriter, ccRW}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class AdvancedSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  private var transaction: StreamTransaction = _

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
      database.people.document.batch.insert(List(
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
        .all
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany"))
        }
    }
    "create a transaction" in {
      database.transaction.begin().map { transaction =>
        this.transaction = transaction
        succeed
      }
    }
    "check the status of the transaction" in {
      database.transaction.status(transaction).map { status =>
        status should be(TransactionStatus.Running)
      }
    }
    "abort the transaction" in {
      database.transaction.abort(transaction).map { status =>
        status should be(TransactionStatus.Aborted)
      }
    }
    "create a second transaction" in {
      database.transaction.begin(locks = List(
        database.people -> TransactionLock.Write
      )).map { transaction =>
        this.transaction = transaction
        succeed
      }
    }
    "insert two records in a transaction" in {
      database.people.document.batch.insert(List(
        Person("Charles", 35),
        Person("Donna", 41)
      ), transaction = transaction).map { _ =>
        succeed
      }
    }
    "verify the records aren't visible before commit" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} RETURN p")
        .all
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany"))
        }
    }
    "commit the transaction" in {
      database.transaction.commit(transaction).map { status =>
        status should be(TransactionStatus.Committed)
      }
    }
    "verify the four records" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} RETURN p")
        .all
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany", "Charles", "Donna"))
          people.map(_.age).toSet should be(Set(21, 19, 35, 41))
        }
    }
    "update the records using DSL" in {
      database
        .people
        .update(Person.age is 21, Person.age(22))
        .map { modified =>
          modified should be(1L)
        }
    }
  }

  object database extends Graph("advanced") {
    val people: DocumentCollection[Person] = vertex[Person](Person)
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