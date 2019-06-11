package spec

import com.outr.arango.rest.EdgeDefinition
import com.outr.arango.{ArangoDB, ArangoSession}
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.concurrent.Future

class GraphSpec extends AsyncWordSpec with Matchers {
  private var session: ArangoSession = _
  private var db: ArangoDB = _

  "Graphs" should {
    "create the session" in {
      ArangoSession.default.map { s =>
        session = s
        db = session.db()
        s.token shouldNot be("")
      }
    }
    "list all the graphs" in {
      db.graph.list().map { result =>
        result.error should be(false)
        result.code should be(200)
        result.graphs should be(Nil)
      }
    }
    "create a graph" in {
      db.graph("test").create(edgeDefinitions = List(
        EdgeDefinition("edges", from = List("startVertices"), to = List("endVertices"))
      )).map { response =>
        response.error should be(false)
      }
    }
    "delete a graph" in {
      db.graph("test").delete(dropCollections = true).map { response =>
        response.error should be(false)
      }
    }
    "dispose the session" in {
      Future {
        session.instance.dispose()
        session.instance.isDisposed should be(true)
      }
    }
  }
}
