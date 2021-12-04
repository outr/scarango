package spec

import com.outr.arango._
import com.outr.arango.query.{AQLInterpolator, Query}
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
      val p = Person.ref

      val query = aql {
        FOR (p) IN database.people
        SORT (p.age.desc)
        RETURN (p)
      }

      query.string should be(
      """FOR p IN people
        |SORT p.age DESC
        |RETURN p""".stripMargin)
    }
    "build a query with a filter" in {
      val p = Person.ref

      val query = aql {
        FOR (p) IN database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        RETURN (p)
      }
      val expected = Query(
        "FOR p IN people ",
        "FILTER ", aql"p.age == ${21}", " && ", aql"p.name != ${"Adam"} ",
        "RETURN p"
      )
      query should be(expected)
    }
    "build a query with a remove" in {
      val p = Person.ref

      val query = aql {
        FOR (p) IN database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        REMOVE (p) IN database.people
      }
      val expected = Query(
        "FOR p IN people ",
        aql"FILTER p.age == ${21} && p.name != ${"Adam"} ",
        "REMOVE p IN people")
      query should be(expected)
    }
    "build an update query" in {
      val p = Person.ref

      val query = aql {
        FOR(p) IN database.people
        FILTER ((p.age is 21) && (p.name isNot "Adam"))
        UPDATE (p, p.age(22))
        RETURN (NEW)
      }
      val expected = Query(
        "FOR p IN people ",
        aql"FILTER p.age == ${21} && p.name != ${"Adam"} ",
        aql"UPDATE p WITH {age: ${22}} IN people ",
        "RETURN NEW")
      query should be(expected)
    }
    "build a query to return result count" in {
      val p = Person.ref
      val count = ref("count")
      val query = aql {
        FOR (p) IN database.people
        FILTER (p.age >= 20)
        COLLECT WITH COUNT INTO count
        RETURN (count)
      }
      val expected = Query(
        "FOR p IN people ",
        aql"FILTER p.age >= ${20} ",
        "COLLECT WITH COUNT INTO count ",
        "RETURN count"
      )
      query should be(expected)
    }
  }

  object database extends Graph(name = "advanced") {
    val people: DocumentCollection[Person] = vertex[Person](Person)
  }

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    override implicit val rw: ReaderWriter[Person] = ccRW

    val name: Field[String] = Field[String]("name")
    val age: Field[Int] = Field[Int]("age")

    override def indexes: List[Index] = Nil

    def ref: DocumentRef[Person, Person.type] = DocumentRef(this, Some("p"))

    override val collectionName: String = "people"
  }
}