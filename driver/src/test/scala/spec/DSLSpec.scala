package spec

import com.outr.arango.{Collection, Document, DocumentModel, DocumentRef, Field, Graph, Id, Index, Query, Serialization}
import com.outr.arango.query._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class DSLSpec extends AsyncWordSpec with Matchers {
  "DSL" should {
    "initialize configuration" in {
      Profig.initConfiguration().map { _ =>
        succeed
      }
    }
    "build a simple query" in {
      val p = Person.ref

      val query = aql {
        FOR (p) IN Database.people
        SORT (p.age.desc)
        RETURN (p)
      }
      query should be(Query(
        """FOR p IN people
          |SORT p.age DESC
          |RETURN p""".stripMargin, Map.empty))
    }
    "build a query with a filter" in {
      val p = Person.ref

      val query = aql {
        FOR (p) IN Database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        RETURN (p)
      }
      query should be(Query(
        """FOR p IN people
          |FILTER p.age == @arg1 && p.name != @arg2
          |RETURN p""".stripMargin, Map("arg1" -> 21, "arg2" -> "Adam")
      ))
    }
    "build a query with a remove" in {
      val p = Person.ref

      val query = aql {
        FOR (p) IN Database.people
        FILTER((p.age is 21) && (p.name isNot "Adam"))
        REMOVE (p) IN Database.people
      }
      query should be(Query(
        """FOR p IN people
          |FILTER p.age == @arg1 && p.name != @arg2
          |REMOVE p IN people""".stripMargin, Map("arg1" -> 21, "arg2" -> "Adam")
      ))
    }
    "build an update query" in {
      val p = Person.ref

      val query = aql {
        FOR(p) IN Database.people
        FILTER ((p.age is 21) && (p.name isNot "Adam"))
        UPDATE (p, p.age(22))
        RETURN (NEW)
      }
      query should be(Query(
        """FOR p IN people
          |FILTER p.age == @arg1 && p.name != @arg2
          |UPDATE p WITH {age: @arg3} IN people
          |RETURN NEW""".stripMargin, Map("arg1" -> 21, "arg2" -> "Adam", "arg3" -> 22)))
    }
    "build a query to return result count" in {
      val p = Person.ref
      val count = ref("count")
      val query = aql {
        FOR (p) IN Database.people
        FILTER (p.age >= 20)
        COLLECT WITH COUNT INTO count
        RETURN (count)
      }
      query should be(Query(
        """FOR p IN people
          |FILTER p.age >= @arg1
          |COLLECT WITH COUNT INTO count
          |RETURN count""".stripMargin, Map("arg1" -> 20)))
    }
  }

  object Database extends Graph(databaseName = "advanced") {
    val people: Collection[Person] = vertex[Person]
  }

  case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

  object Person extends DocumentModel[Person] {
    val name: Field[String] = Field[String]("name")
    val age: Field[Int] = Field[Int]("age")

    override def indexes: List[Index] = Nil

    def ref: DocumentRef[Person, Person.type] = DocumentRef(this, Some("p"))

    override val collectionName: String = "people"
    override implicit val serialization: Serialization[Person] = Serialization.auto[Person]
  }
}