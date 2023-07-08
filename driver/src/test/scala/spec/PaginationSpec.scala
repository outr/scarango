package spec

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango.{Document, DocumentModel, Field, Graph, Id, Index}
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.pagination.{PaginationSupport, ResultType}
import com.outr.arango.query.dsl._
import com.outr.arango.query._
import fabric.rw.RW
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

import scala.concurrent.duration.DurationInt

class PaginationSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  private var queryIdRef: Id[Query] = _
  private var queryIdCache: Id[Query] = _
  private var queryIdCU: Id[Query] = _

  "Pagination" should {
    "initialize configuration" in {
      Profig.initConfiguration()
      succeed
    }
    "initialize database" in {
      database.init(dropDatabase = true).map { _ =>
        database.initialized should be(true)
      }
    }
    "insert some colors" in {
      val colors = List(
        "Red", "Blue", "Green", "Orange", "White", "Pink",
        "Purple", "Gray", "Maroon", "Azure", "Aquamarine",
        "Brown", "Yellow", "Cyan", "Magenta"
      ).map(name => Color(name))
      database.colors.batch.insert(colors).map(_ => succeed)
    }
    "create a paginated AQL interpolated query with reference" in {
      assertCreatePage(ResultType.Reference).map(queryIdRef = _)
    }
    "create a paginated AQL interpolated query with cached" in {
      assertCreatePage(ResultType.Cached).map(queryIdCache = _)
    }
    "create a paginated AQL interpolated query with cached updated" in {
      assertCreatePage(ResultType.CachedUpdated).map(queryIdCU = _)
    }
    "modify a Color" in {
      database.colors.update { c =>
        (c.name is "Aquamarine") -> List(
          c.name("Aqua")
        )
      }.map { modified =>
        modified should be(1)
      }
    }
    "delete a Color" in {
      database.colors.query.byFilter(c => c.name === "Blue").one.flatMap { color =>
        database.colors.delete(color._id).map(_ => ())
      }
    }
    "verify expected results from existing cache with reference" in {
      assertRefreshPage(queryIdRef, List("Aqua", "Azure"))
    }
    "verify expected results from existing cache with cached" in {
      assertRefreshPage(queryIdCache, List("Aquamarine", "Azure", "Blue"))
    }
    "verify expected results from existing cache with cached updated" in {
      assertRefreshPage(queryIdCU, List("Aqua", "Azure", "Blue"))
    }
    "verify stored records in PagedResults" in {
      database.pagedResults.query.count.map { count =>
        count should be > 0
      }
    }
    "manually execute maintenance" in {
      IO.sleep(2.seconds).flatMap { _ =>
        database.pagination.doMaintenance()
      }
    }
    "verify stored records is empty in PagedResults" in {
      database.pagedResults.query.count.map { count =>
        count should be(0)
      }
    }
  }

  private def assertCreatePage(resultType: ResultType): IO[Id[Query]] = {
    val query =
      aql"""
            FOR c IN ${database.colors}
            SORT c.${Color.name} ASC
            RETURN c
           """
    database.pagination[Color](
      query = query,
      pageSize = 2,
      resultType = resultType,
      ttl = 2.second
    ).map { pageOption =>
      val page = pageOption.get
      page.page should be(0)
      page.pageSize should be(2)
      page.total should be(15)
      page.pages should be(8)
      page.entries.flatten.map(_.name) should be(List("Aquamarine", "Azure"))
      page.queryId
    }
  }

  private def assertRefreshPage(queryId: Id[Query], expected: List[String]): IO[Assertion] = {
    database.pagination.load[Color](
      queryId = queryId,
      page = 0,
      pageSize = 3
    ).map { pageOption =>
      val page = pageOption.get
      page.page should be(0)
      page.pageSize should be(3)
      page.total should be(15)
      page.pages should be(5)
      page.entries.flatten.map(_.name) should be(expected)
    }
  }

  object database extends Graph(name = "paginationTest") with PaginationSupport {
    val colors: DocumentCollection[Color, Color.type] = vertex(Color)
  }

  case class Color(name: String, _id: Id[Color] = Color.id()) extends Document[Color]

  object Color extends DocumentModel[Color] {
    override val collectionName: String = "colors"

    val name: Field[String] = field("name")

    override implicit val rw: RW[Color] = RW.gen

    override def indexes: List[Index] = Nil
  }
}