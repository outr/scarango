package spec

import com.outr.arango.{ArangoCollection, ArangoDB, ArangoSession}
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.concurrent.Future
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._

class ActorsAndMoviesSpec extends AsyncWordSpec with Matchers {
  implicit val movieEncoder: Encoder[Movie] = deriveEncoder[Movie]
  implicit val movieDecoder: Decoder[Movie] = deriveDecoder[Movie]
  implicit val actorEncoder: Encoder[Actor] = deriveEncoder[Actor]
  implicit val actorDecoder: Decoder[Actor] = deriveDecoder[Actor]
  implicit val actsInEncoder: Encoder[ActsIn] = deriveEncoder[ActsIn]
  implicit val actsInDecoder: Decoder[ActsIn] = deriveDecoder[ActsIn]

  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var actors: ArangoCollection = _
  private var movies: ArangoCollection = _
  private var actsIn: ArangoCollection = _

  private var theMatrix1: Movie = _
  private var theMatrix2: Movie = _
  private var theMatrix3: Movie = _

  private var keanu: Actor = _
  private var carrie: Actor = _
  private var laurence: Actor = _
  private var hugo: Actor = _
  private var emil: Actor = _

  "Actors and Movies" when {
    "initializing" should {
      "create the session and database" in {
        ArangoSession.default.map { s =>
          session = s
          db = session.db("_system")
          actors = db.collection("actors")
          movies = db.collection("movies")
          actsIn = db.collection("actsIn")
          s.token shouldNot be("")
        }
      }
    }
    "creating collections" should {
      "create the actors collection" in {
        actors.create(waitForSync = true).map { response =>
          response.error should be(false)
        }
      }
      "create the movies collection" in {
        movies.create(waitForSync = true).map { response =>
          response.error should be(false)
        }
      }
      "create the actsIn edge collection" in {
        actsIn.create(waitForSync = true, `type` = ArangoCollection.Edges).map { response =>
          response.error should be(false)
        }
      }
    }
    "creating the movies" should {
      "add The Matrix" in {
        movies.document.create(Movie(
          _key = "TheMatrix",
          title = "The Matrix",
          released = 1999,
          tagline = "Welcome to the Real World"
        ), returnNew = true).map { result =>
          result.`new` shouldNot be(None)
          theMatrix1 = result.`new`.get
          theMatrix1._key should be("TheMatrix")
          theMatrix1._id shouldNot be(None)
          theMatrix1._rev shouldNot be(None)
        }
      }
    }
    "creating the actors" should {
      "add Keanu Reeves" in {
        actors.document.create(Actor(
          _key = "Keanu",
          name = "Keanu Reeves",
          born = 1964
        ), returnNew = true).map { result =>
          result.`new` shouldNot be(None)
          keanu = result.`new`.get
          keanu._key should be("Keanu")
          keanu._id shouldNot be(None)
          keanu._rev shouldNot be(None)
        }
      }
    }
//    "creating edges" should {
//      "connect everyone for The Matrix 1" in {
//        actsIn.
//      }
//    }
    "cleanup" should {
      "drop the actsIn collection" in {
        actsIn.drop().map { response =>
          response.error should be(false)
        }
      }
      "drop the movies collection" in {
        movies.drop().map { response =>
          response.error should be(false)
        }
      }
      "drop the actors collection" in {
        actors.drop().map { response =>
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
}

case class Movie(_key: String, title: String, released: Int, tagline: String, _id: Option[String] = None, _rev: Option[String] = None)

case class Actor(_key: String, name: String, born: Int, _id: Option[String] = None, _rev: Option[String] = None)

case class ActsIn(roles: List[String], year: Int, _id: Option[String] = None, _key: Option[String] = None, _rev: Option[String] = None)