package spec

import com.outr.arango.api.{OperationType, WALOperation, WALOperations}
import com.outr.arango.{ArangoDB, Credentials, DatabaseState}
import io.youi.http.Headers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

import scala.concurrent.Promise
import scala.concurrent.duration._

class ArangoDatabaseSpec extends AsyncWordSpec with Matchers {
  private lazy val db = new ArangoDB()

  "ArangoDatabase" should {
    "initialize configuration" in {
      Profig.initConfiguration().map { _ =>
        succeed
      }
    }
    "fail to initialize with bad password" in {
      val db = new ArangoDB(credentials = Some(Credentials("root", "bad")))
      db.init().map { state =>
        state shouldBe a[DatabaseState.Error]
        succeed
      }
    }
    "initialize successfully" in {
      db.init().map { state =>
        state shouldBe a[DatabaseState.Initialized]
        db.session.client.request.headers.first(Headers.Request.Authorization) should not be None
      }
    }
    "get the current database" in {
      db.api.db.current.map { response =>
        response.value.name should be("_system")
      }
    }
    "list the databases" in {
      db.api.db.list().map { response =>
        response.value should contain("_system")
      }
    }
    "create a test database" in {
      db.api.db("databaseExample").create().map { response =>
        response.value should be(true)
      }
    }
    "verify the database was created" in {
      db.api.db.list().map { response =>
        response.value should contain("databaseExample")
      }
    }
    "check the WAL" in {
      Thread.sleep(1000)
      db.api.db("databaseExample").wal.tail().flatMap { ops =>
        ops.operations.length should be(1)
        val op = ops.operations.head
        op.`type` should be(OperationType.CreatedDatabase)
        op.db should be("databaseExample")
        ops.tail().map { moreOps =>
          moreOps.operations.isEmpty should be(true)
        }
      }
    }
    "check the WAL using monitor" in {
      val monitor = db.api.db("databaseExample").wal.monitor(delay = 1.second, skipHistory = false)
      var first = Option.empty[WALOperations]
      var second = Option.empty[WALOperations]
      val promise = Promise[Unit]()
      var list = List.empty[WALOperation]
      monitor.attach { op =>
        list = op :: list
      }
      monitor.tailed.attach { ops =>
        if (first.isEmpty) {
          first = Some(ops)
        } else if (second.isEmpty) {
          second = Some(ops)
        } else {
          monitor.stop()
          promise.success(())
        }
      }
      promise.future.map { _ =>
        first.isEmpty should be(false)
        val f = first.get
        f.operations.length should be(1)
        second.isEmpty should be(false)
        val s = second.get
        s.operations.length should be(0)
        list.length should be(1)
      }
    }
    "drop the test database" in {
      db.api.db("databaseExample").drop().map { response =>
        response.value should be(true)
      }
    }
    "verify the database dropped" in {
      db.api.db.list().map { response =>
        response.value should not contain "databaseExample"
      }
    }
  }
}