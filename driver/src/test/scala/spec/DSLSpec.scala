package spec

import com.outr.arango.{Collection, Document, DocumentModel, DocumentRef, Field, Graph, Id, Index, Query, Serialization}
import com.outr.arango.aql._
import org.scalatest.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

class DSLSpec extends AsyncWordSpec with Matchers {
  "DSL" should {
    "initialize configuration" in {
      Profig.loadDefaults()
      succeed
    }
    "build a simple query" in {
      val p = Person.ref

      val q = (
        FOR (p) IN Database.people
        SORT p.age.desc
        RETURN p
      )
      q.toQuery should be(Query(
        """FOR p1 IN people
          |SORT p1.age DESC
          |RETURN p1""".stripMargin, Map.empty))
    }
    "build a query with a filter" in {
      val p = Person.ref

      val query = (
        FOR (p) IN Database.people
        FILTER (p.age is 21) && (p.name isNot "Adam")
        RETURN p
      )
      query.toQuery should be(Query(
        """FOR p1 IN people
          |FILTER p1.age == @a1 && p1.name != @a2
          |RETURN p1""".stripMargin, Map("a1" -> 21, "a2" -> "Adam")
      ))
    }
    "build an update query" in {
      val p = Person.ref

      val query = (
        FOR (p) IN Database.people
        FILTER (p.age is 21) && (p.name isNot "Adam")
        UPDATE (p, p.age(22))
        RETURN NEW
      )
      query.toQuery should be(Query(
        """FOR p1 IN people
          |FILTER p1.age == @a1 && p1.name != @a2
          |UPDATE p1 WITH {age: @arg1} IN people
          |RETURN NEW""".stripMargin, Map("a1" -> 21, "a2" -> "Adam", "arg1" -> 22)))
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

    def ref: DocumentRef[Person, Person.type] = DocumentRef(this)

    override val collectionName: String = "people"
    override implicit val serialization: Serialization[Person] = Serialization.auto[Person]
  }
}