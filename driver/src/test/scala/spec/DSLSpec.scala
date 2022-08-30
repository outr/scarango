package spec

import com.outr.arango._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query._
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
      val expected = Query
        .static("FOR p IN people ")
        .static("FILTER ")
        .withQuery(aql"p.age == ${21}")
        .static(" && ")
        .withQuery(aql"p.name != ${"Adam"} ")
        .static("RETURN p")
      query should be(expected)
    }
    "build a query with a remove" in {
      val p = Person.ref

      val query = aql {
        FOR (p) IN database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        REMOVE (p) IN database.people
      }
      val expected = Query
        .static("FOR p IN people ")
        .withQuery(aql"FILTER p.age == ${21} && p.name != ${"Adam"} ")
        .static("REMOVE p IN people")
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
      val expectedQuery =
        """FOR p IN people
          |FILTER p.age == @arg0 && p.name != @arg1
          |UPDATE p WITH {
          |  age: @arg2
          |} IN people
          |RETURN NEW""".stripMargin
      query.string should be(expectedQuery)
      query.variables should be(Map("arg0" -> fabric.num(21), "arg1" -> fabric.str("Adam"), "arg2" -> fabric.num(22)))
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
      val expected = Query
        .static("FOR p IN people ")
        .withQuery(aql"FILTER p.age >= ${20} ")
        .static("COLLECT WITH COUNT INTO count ")
        .static("RETURN count")
      query should be(expected)
    }
  }

  object database extends Graph(name = "advanced") {
    val people: DocumentCollection[Person] = vertex[Person](Person)
  }

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    override implicit val rw: ReaderWriter[Person] = ccRW

    val name: Field[String] = field("name")
    val age: Field[Int] = field("age")

    override def indexes: List[Index] = Nil

    def ref: DocumentRef[Person, Person.type] = DocumentRef(this, Some("p"))

    override val collectionName: String = "people"
  }
}