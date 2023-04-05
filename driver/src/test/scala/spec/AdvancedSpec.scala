package spec

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core.{DeleteOptions, StreamTransaction, TransactionLock, TransactionStatus}
import com.outr.arango.query._
import com.outr.arango.query.dsl._
import com.outr.arango.queue.DBQueue
import fabric.rw.RW
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig
import cats.syntax.all._
import com.outr.arango.backup.{DatabaseBackup, DatabaseRestore}

import java.nio.file.Paths
import java.util.concurrent.CompletionException

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
    "fail to insert a duplicate record" in {
      database.people.insert(Person("Bethany", 321)).attempt.map {
        case Left(exc: ArangoException) =>
          exc.contraintViolation should not be None
          val c = exc.contraintViolation.get
          c.field should be("name")
          c.`type` should be("persistent")
        case _ => fail()
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
      database.people.update { p =>
        (p.age is 21) -> List(
          p.age + 1,
          PUSH(p.favoriteNumbers, 21)
        )
      }.map(modified => modified should be(1))
    }
    "verify the age was updated" in {
      database
        .people
        .query
        .byFilter(Person.age is 22)
        .all
        .map { people =>
          people.map(_.name) should be(List("Adam"))
          people.map(_.favoriteNumbers) should be(List(List(21)))
        }
    }
    "append more favorite numbers" in {
      database.people.update { p =>
        (p.age is 22) -> List(
          APPEND(p.favoriteNumbers, List(7, 42))
        )
      }.map(modified => modified should be(1))
    }
    "verify the age is between a range" in {
      database
        .people
        .query
        .byFilter((Person.age < 23) && (Person.age > 21))
        .all
        .map { people =>
          people.map(_.name) should be(List("Adam"))
          people.map(_.favoriteNumbers) should be(List(List(21, 7, 42)))
        }
    }
    "update multiple fields in a record using DSL" in {
      database
        .people
        .update { p =>
          (p.age is 22) -> List(
            Person.bio("I have a new bio!"),
            Person.age(23)
          )
        }
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
      queue
        .insert(database.people, fs2.Stream[IO, Person](people: _*))
        .flatMap { _.finish().map { queue =>
            queue.inserted should be(10)
            queue.upserted should be(0)
            queue.deleted should be(0)
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
    "batch delete" in {
      database.people.query.byFilter(Person.age > 10).all.flatMap { list =>
        list.map(_.name).toSet should be(Set("Bethany", "Donna", "Adam"))
        database.people.batch.delete(list.map(_._id), DeleteOptions(waitForSync = true, silent = false)).map { results =>
          results.documents.length should be(3)
        }
      }
    }
  }

  object database extends Graph("advanced") {
    val people: DocumentCollection[Person, Person.type] = vertex(Person)
  }

  case class Person(name: String,
                    age: Int,
                    bio: String = "",
                    favoriteNumbers: List[Int] = Nil,
                    _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    override implicit val rw: RW[Person] = RW.gen

    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")
    val bio: Field[String] = field[String]("bio").modify(_.reverse, identity)
    val favoriteNumbers: Field[List[Int]] = field("favoriteNumbers")

    override def indexes: List[Index] = List(name.index.persistent(unique = true))

    override val collectionName: String = "people"
  }
}