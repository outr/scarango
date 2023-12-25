package spec

import com.outr.arango._
import com.outr.arango.backup.{DatabaseBackup, DatabaseRestore}
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core.{DeleteOptions, StreamTransaction, TransactionLock, TransactionStatus}
import com.outr.arango.query._
import com.outr.arango.query.dsl._
import com.outr.arango.queue.OperationQueueSupport
import com.outr.arango.upsert.Searchable
import fabric._
import fabric.rw._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

import java.nio.file.Paths

class AdvancedSpec extends AsyncWordSpec with AsyncIOSpec with Matchers with OperationQueueSupport {
  private var transaction: StreamTransaction = _
  private var bethanyLastModified: Long = _



  "Advanced" should {
    "initialize configuration" in {
      Profig.initConfiguration()
      succeed
    }
    "initialize" in {
      database.init(dropDatabase = true).map { _ =>
        succeed
      }
    }
    "truncate the database" in {
      database.truncate().map { _ =>
        succeed
      }
    }
    "verify conversions of basic types" in {
      database
        .query[Json](
        aql"""
            RETURN {
              b: true,
              i: 123,
              d: 1.23,
              s: "1.23",
              j: {
                test: "value"
              }
            }
           """)
        .one
        .map { json =>
          json should be(obj(
            "b" -> true,
            "i" -> 123,
            "d" -> BigDecimal(1.23),
            "s" -> "1.23",
            "j" -> obj(
              "test" -> "value"
            )
          ))
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
        .toList
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany"))
          people.map(_.bio).toSet should be(Set("", "ynahteB m'I\n,iH"))
        }
    }
    "fail to insert a duplicate record" in {
      database.people.insert(Person("Bethany", 321)).attempt.map {
        case Left(exc: ArangoException) =>
          exc.constraintViolation should not be None
          val c = exc.constraintViolation.get
          c.fields should be(Set("name"))
          c.`type` should be("persistent")
        case other => fail(s"Received $other instead of ArangoException!")
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
        .toList
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
        .toList
        .map { people =>
          people.map(_.name).toSet should be(Set("Adam", "Bethany", "Charles", "Donna"))
          people.map(_.age).toSet should be(Set(21, 19, 35, 41))
        }
    }
    "verify fullCount works" in {
      database
        .people
        .query(aql"FOR p IN ${database.people} LIMIT 1 RETURN p")
        .withCount()
        .withFullCount()
        .cursor()
        .map { cursor =>
          val people = cursor.toList
          people.map(_.name) should be(List("Adam"))
          cursor.count should be(1L)
          cursor.fullCount should be(4L)
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
          .toList
          .map { people =>
            people.map(_.name).toSet should be(Set("Adam", "Bethany", "Donna"))
            people.map(_.age).toSet should be(Set(21, 19, 41))
          }
      }
    }
    "update the records using DSL" in {
      database.people.update.toList { p =>
        (p.age is 21) -> List(
          p.age + 1,
          PUSH(p.favoriteNumbers, 21),
          p.extra(obj("test1" -> "Success!"))
        )
      }.map { modified =>
        modified.map(_.name) should be(List("Adam"))
        modified.map(_.age) should be(List(22))
        modified.map(_.favoriteNumbers) should be(List(List(21)))
        modified.map(_.extra) should be(List(obj("test1" -> "Success!")))
      }
    }
    "verify the age was updated" in {
      database
        .people
        .query
        .byFilter(_.age is 22)
        .toList
        .map { people =>
          people.map(_.name) should be(List("Adam"))
          people.map(_.favoriteNumbers) should be(List(List(21)))
          people.map(_.extra) should be(List(obj("test1" -> "Success!")))
        }
    }
    "append more favorite numbers" in {
      database.people.update.withOptions(mergeObjects = false).toList { p =>
        (p.age is 22) -> List(
          APPEND(p.favoriteNumbers, List(7, 42)),
          p.extra(obj("test2" -> "Replaced!"))
        )
      }.map { people =>
        people.map(_.name) should be(List("Adam"))
        people.map(_.favoriteNumbers) should be(List(List(21, 7, 42)))
        people.map(_.extra) should be(List(obj("test2" -> "Replaced!")))
      }
    }
    "verify the age is between a range" in {
      database
        .people
        .query
        .byFilter(p => (p.age < 23) && (p.age > 21))
        .toList
        .map { people =>
          people.map(_.name) should be(List("Adam"))
          people.map(_.favoriteNumbers) should be(List(List(21, 7, 42)))
          people.map(_.extra) should be(List(obj("test2" -> "Replaced!")))
        }
    }
    "update multiple fields in a record using DSL" in {
      database
        .people
        .update.toList { p =>
          (p.age is 22) -> List(
            Person.bio("I have a new bio!"),
            Person.age(23)
          )
        }.map { people =>
          people.map(_.bio) should be(List("I have a new bio!"))
          people.map(_.age) should be(List(23))
        }
    }
    "use the queue support to insert multiple records" in {
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
      val upsert = database.people.op.createUpsertReplace(p => List(
        Searchable(Person.name, p.name)
      ))
      upsert(people: _*)
        .flatMap(_ => flushQueue())
        .map { _ =>
          upsert.processed should be(10)
        }
    }
    "verify the DBQueue properly inserted the records" in {
      database.people.query.toList.map(_.map(_.name).toSet).map { set =>
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
      database.people.query.toList.map { people =>
        people should be(Nil)
      }
    }
    "do a database restore" in {
      DatabaseRestore(database, Paths.get("backup"), truncate = false, upsert = false).map { _ =>
        succeed
      }
    }
    "verify all the records are restored" in {
      database.people.query.toList.map(_.map(_.name).toSet).map { names =>
        names should be(Set(
          "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Bethany", "Donna", "Adam"
        ))
      }
    }
    "check the modified stamp on Bethany" in {
      database.people.query.byFilter(p => p.name === "Bethany").one.map { person =>
        person.name should be("Bethany")
        bethanyLastModified = person.modified
      }
    }
    "modify Bethany's bio" in {
      database.people.update { p =>
        (p.name === "Bethany") -> List(
          p.bio("Updated bio!")
        )
      }.map { modified =>
        modified should be(1)
      }
    }
    "verify the modified stamp has been updated" in {
      database.people.query.byFilter(p => p.name === "Bethany").one.map { person =>
        person.name should be("Bethany")
        person.modified should be > bethanyLastModified
      }
    }
    "upsert Donna's bio" in {
      database.people.upsert
        .withSearch(Person.name("Donna"))
        .withInsert(Person(name = "Donna", age = 41, bio = "New Record"))
        .withReplace(Person(name = "Donna", age = 41, bio = "Replaced!"))
        .toList
        .map { results =>
          results.length should be(1)
          val result = results.head
          result.original should not be None
          result.original.get.name should be("Donna")
          result.newValue.name should be("Donna")
          result.newValue.age should be(41)
          result.newValue.bio should be("Replaced!")
        }
    }
    "upsert multiple bios" in {
      database.people.upsert
        .withListSearch(List(Person("Bethany", 30), Person("Adam", 30), Person("Tom", 30))) { p =>
          List(Searchable.Filter(Person.name, p.name))
        }
        .withNoUpdate
        .toList
        .map { list =>
          list.length should be(3)
          val names = list.map(_.newValue.name).toSet
          names should be(Set("Bethany", "Adam", "Tom"))
          val ages = list.map(_.newValue.age).toSet
          ages should be(Set(19, 23, 30))
        }
    }
    "batch delete" in {
      database.people.query.byFilter(_.age > 10).toList.flatMap { list =>
        list.map(_.name).toSet should be(Set("Bethany", "Donna", "Adam", "Tom"))
        database.people.batch.delete(list.map(_._id), DeleteOptions(waitForSync = true, silent = false)).map { results =>
          results.documents.length should be(4)
        }
      }
    }
  }

  override protected def opFlushSize: Int = 3

  override protected def opChunkSize: Int = 3

  object database extends Graph("advanced") {
    val people: DocumentCollection[Person, Person.type] = vertex(Person)
  }
}