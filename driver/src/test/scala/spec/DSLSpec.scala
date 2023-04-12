package spec

import com.outr.arango._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query.dsl._
import fabric.rw._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class DSLSpec extends AsyncWordSpec with Matchers {
  "DSL" should {
    "initialize configuration" in {
      Profig.initConfiguration()
      succeed
    }
    "build a simple query" in {
      val p = database.people.ref

      val query = aql {
        FOR (p) IN database.people
        SORT (p.age.desc)
        RETURN (p)
      }

      query.string should be(
      """FOR ref1 IN people
        |SORT ref1.age DESC
        |RETURN ref1""".stripMargin)
    }
    "build a query with a filter" in {
      val p = database.people.ref

      val query = aql {
        FOR (p) IN database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        RETURN (p)
      }
      val expected =
        """FOR ref1 IN people
          |FILTER ref1.age == @arg0 && ref1.name != @arg1
          |RETURN ref1""".stripMargin
      query.string should be(expected)
    }
    "build a query with a remove" in {
      val p = database.people.ref

      val query = aql {
        FOR (p) IN database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        REMOVE (p) IN database.people
      }
      val expected =
        """FOR ref1 IN people
          |FILTER ref1.age == @arg0 && ref1.name != @arg1
          |REMOVE ref1 IN people""".stripMargin
      query.string should be(expected)
    }
    "build an update query" in {
      val p = database.people.ref

      val query = aql {
        FOR(p) IN database.people
        FILTER ((p.age is 21) && (p.name isNot "Adam"))
        UPDATE (p, p.age(22))
        RETURN (NEW)
      }
      val expectedQuery =
        """FOR ref1 IN people
          |FILTER ref1.age == @arg0 && ref1.name != @arg1
          |UPDATE ref1 WITH {
          |  age: @arg2
          |} IN people
          |RETURN NEW""".stripMargin
      query.string should be(expectedQuery)
      query.variables should be(Map("arg0" -> fabric.num(21), "arg1" -> fabric.str("Adam"), "arg2" -> fabric.num(22)))
    }
    "build a query to return result count" in {
      val p = database.people.ref
      val count = NamedRef("count")
      val query = aql {
        FOR (p) IN database.people
        FILTER (p.age >= 20)
        COLLECT WITH COUNT INTO count
        RETURN (count)
      }
      val expected =
        """FOR ref1 IN people
          |FILTER ref1.age >= @arg0
          |COLLECT WITH COUNT INTO count
          |RETURN count""".stripMargin
      query.string should be(expected)
    }
    "build a query with two refs" in {
      val r1 = database.people.ref
      val r2 = database.people.ref
      r1.hashCode() should not be r2.hashCode()
      val query = aql {
        LET(r1) := DOCUMENT(Person.id("123"))
        FOR(r2) IN database.people
        FILTER(r1._id is r2._id)
        RETURN(r2)
      }
      val expected =
        """LET ref1 = DOCUMENT(@arg0)
          |FOR ref2 IN people
          |FILTER ref1._id == ref2._id
          |RETURN ref2""".stripMargin
      query.string should be(expected)
    }
  }

  object database extends Graph(name = "advanced") {
    val people: DocumentCollection[Person, Person.type] = vertex(Person)
  }

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    override implicit val rw: RW[Person] = RW.gen

    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")

    override def indexes: List[Index] = Nil

    override val collectionName: String = "people"
  }
}