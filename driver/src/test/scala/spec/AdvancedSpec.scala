package spec

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core.{StreamTransaction, TransactionLock, TransactionStatus}
import com.outr.arango.query._
import com.outr.arango.query.dsl._
import com.outr.arango.queue.DBQueue
import fabric.rw.{ReaderWriter, ccRW}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig
import cats.syntax.all._
import com.outr.arango.backup.{DatabaseBackup, DatabaseRestore}

import java.nio.file.Paths

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
      database.people.batch.insert(List(
        Person("Adam", 21),
        Person("Bethany", 19, "Hi,\nI'm Bethany")
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
          people.map(_.bio).toSet should be(Set("", "ynahteB m'I\n,iH"))
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
      database.people.batch.insert(List(
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
    "delete a single record via AQL" in {
      database.execute(
        aql"""
            FOR p IN ${database.people}
            FILTER p.${Person.age} == 35
            REMOVE p IN ${database.people}
           """).flatMap { _ =>
        database
          .people
          .query(aql"FOR p IN ${database.people} RETURN p")
          .all
          .map { people =>
            people.map(_.name).toSet should be(Set("Adam", "Bethany", "Donna"))
            people.map(_.age).toSet should be(Set(21, 19, 41))
          }
      }
    }
    "update the records using DSL" in {
      database
        .people
        .modify(Person.age is 21, Person.age(22))
        .map { modified =>
          modified should be(1)
        }
    }
    "verify the age was updated" in {
      database
        .people
        .query
        .byFilter(Person.age is 22)
        .all
        .map { people =>
          people.map(_.name) should be(List("Adam"))
        }
    }
    "update multiple fields in a record using DSL" in {
      database
        .people
        .modify(Person.age is 22, Person.bio("I have a new bio!"), Person.age(23))
        .map { modified =>
          modified should be(1)
        }
    }
    "use the DBQueue to insert multiple records" in {
      val queue = DBQueue(3)
      val people = List(
        Person("One", 1),
        Person("Two", 2),
        Person("Three", 3),
        Person("Four", 4),
        Person("Five", 5),
        Person("Six", 6),
        Person("Seven", 7),
        Person("Eight", 8),
        Person("Nine", 9),
        Person("Ten", 10),
      )
      fs2.Stream[IO, Person](people: _*)
        .evalScan(queue)((queue, person) => queue.insert(person -> database.people))
        .compile
        .lastOrError
        .flatMap { queue =>
          queue.finish().map { _ =>
            queue.inserts should be(10)
            queue.upserts should be(0)
            queue.deletes should be(0)
          }
        }
    }
    "verify the DBQueue properly inserted the records" in {
      database.people.query.all.map(_.map(_.name).toSet).map { set =>
        set should be(Set(
          "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Bethany", "Donna", "Adam"
        ))
      }
    }
    "do a database backup" in {
      DatabaseBackup(database, Paths.get("backup")).map { _ =>
        succeed
      }
    }
    "truncate the database before restoring" in {
      database.people.collection.truncate().map { _ =>
        succeed
      }
    }
    "verify the collection is empty" in {
      database.people.query.all.map { people =>
        people should be(Nil)
      }
    }
    "do a database restore" in {
      DatabaseRestore(database, Paths.get("backup"), truncate = false, upsert = false).map { _ =>
        succeed
      }
    }
    "verify all the records are restored" in {
      database.people.query.all.map(_.map(_.name).toSet).map { names =>
        names should be(Set(
          "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Bethany", "Donna", "Adam"
        ))
      }
    }
  }

  object database extends Graph("advanced") {
    val people: DocumentCollection[Person] = vertex[Person](Person)
  }

  case class Person(name: String,
                    age: Int,
                    bio: String = "",
                    _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    override implicit val rw: ReaderWriter[Person] = ccRW

    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")
    val bio: Field[String] = field[String]("bio").modify(_.reverse, identity)

    override def indexes: List[Index] = Nil

    override val collectionName: String = "people"
  }
}