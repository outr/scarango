package spec

import com.outr.arango._
import com.outr.arango.rest.Edge
import com.outr.arango.{ArangoDB, ArangoEdge, ArangoGraph, ArangoSession, ArangoVertex}
import org.scalatest.{Assertion, AsyncWordSpec, Matchers}

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
  implicit val movieAndActorsDecoder: Decoder[MovieAndActors] = deriveDecoder[MovieAndActors]

  private var session: ArangoSession = _
  private var db: ArangoDB = _
  private var graph: ArangoGraph = _
  private var actors: ArangoVertex = _
  private var movies: ArangoVertex = _
  private var actsIn: ArangoEdge = _

  private var theMatrix1: Movie = _
  private var theMatrix2: Movie = _
  private var theMatrix3: Movie = _

  private var keanu: Actor = _
  private var carrie: Actor = _
  private var laurence: Actor = _
  private var hugo: Actor = _
  private var emil: Actor = _

  def save(actor: Actor)(updated: Actor => Unit): Future[Assertion] = {
    actors.insert(actor).map { result =>
      val a = actor.copy(
        _id = Some(result.vertex._id),
        _key = result.vertex._key,
        _rev = Some(result.vertex._rev)
      )
      updated(a)
      a._key should be(actor._key)
      a._id should be(Some(s"actors/${actor._key}"))
      a._rev shouldNot be(None)
    }
  }

  "Actors and Movies" when {
    "initializing" should {
      "create the session and database" in {
        ArangoSession.default.flatMap { s =>
          session = s
          db = session.db()
          graph = db.graph("hollywood")
          graph.create().flatMap { result =>
            actors = graph.vertex("actors")
            movies = graph.vertex("movies")
            actsIn = graph.edge("actsIn")

            result.error should be(false)
          }
        }
      }
    }
    "creating collections" should {
      "create the actors collection" in {
        actors.create().map { response =>
          response.error should be(false)
        }
      }
      "create the movies collection" in {
        movies.create().map { response =>
          response.error should be(false)
        }
      }
      "create the actsIn edge collection" in {
        actsIn.create(actors, movies).map { response =>
          response.error should be(false)
        }
      }
    }
    "creating the movies" should {
      "add The Matrix" in {
        val movie = Movie(
          _key = "TheMatrix",
          title = "The Matrix",
          released = 1999,
          tagline = "Welcome to the Real World"
        )
        movies.insert(movie).map { result =>
          result.error should be(false)
          theMatrix1 = movie.copy(
            _id = Some(result.vertex._id),
            _key = result.vertex._key,
            _rev = Some(result.vertex._rev)
          )
          theMatrix1._key should be("TheMatrix")
          theMatrix1._id should be(Some("movies/TheMatrix"))
          theMatrix1._rev shouldNot be(None)
        }
      }
    }
    "creating the actors" should {
      "add Neo" in {
        save(Actor(
          _key = "Keanu",
          name = "Keanu Reeves",
          born = 1964
        ))(keanu = _)
      }
      "add Trinity" in {
        save(Actor(
          _key = "Carrie",
          name = "Carri-Ann Moss",
          born = 1967
        ))(carrie = _)
      }
      "add Morpheus" in {
        save(Actor(
          _key = "Laurence",
          name = "Laurence Fishburne",
          born = 1961
        ))(laurence = _)
      }
      "add Agent Smith" in {
        save(Actor(
          _key = "Hugo",
          name = "Hugo Weaving",
          born = 1960
        ))(hugo = _)
      }
      "add Emil" in {
        save(Actor(
          _key = "Emil",
          name = "Emil Eifrem",
          born = 1978
        ))(emil = _)
      }
    }
    "creating edges" should {
      "connect Keanu for The Matrix 1" in {
        actsIn.insert(ActsIn("Cast", keanu._id.get, theMatrix1._id.get, List("Neo"), 1999)).map { result =>
          result.error should be(false)
        }
      }
      "connect Carrie for The Matrix 1" in {
        actsIn.insert(ActsIn("Cast", carrie._id.get, theMatrix1._id.get, List("Trinity"), 1999)).map { result =>
          result.error should be(false)
        }
      }
      "connect Laurence for The Matrix 1" in {
        actsIn.insert(ActsIn("Cast", laurence._id.get, theMatrix1._id.get, List("Morpheus"), 1999)).map { result =>
          result.error should be(false)
        }
      }
      "connect Hugo for The Matrix 1" in {
        actsIn.insert(ActsIn("Cast", hugo._id.get, theMatrix1._id.get, List("Agent Smith"), 1999)).map { result =>
          result.error should be(false)
        }
      }
      "connect Emil for The Matrix 1" in {
        actsIn.insert(ActsIn("Cast", emil._id.get, theMatrix1._id.get, List("Emil"), 1999)).map { result =>
          result.error should be(false)
        }
      }
    }
    "querying" should {
      "find all actors who acted in 'The Matrix'" in {
        val query = aql"FOR x IN ANY ${theMatrix1._id.get} actsIn OPTIONS {bfs: true, uniqueVertices: 'global'} RETURN x"
        db.cursor[Actor](query, count = true).map { response =>
          response.error should be(false)
          response.count should be(Some(5))
        }
      }
      "find all movies and actors that acted in them" in {
        val query =
          aql"""
               FOR movie
               IN movies
               LET cast = (FOR actor IN INBOUND movie._id actsIn RETURN actor)
               RETURN MERGE(movie, { actors: cast })
             """
        db.cursor[MovieAndActors](query, count = true).map { response =>
          response.error should be(false)
          response.count should be(Some(1))
          val movie = response.result.head
          movie._key should be("TheMatrix")
          movie.actors.length should be(5)
        }
      }
    }
    "cleanup" should {
      "drop the movies collection" in {
        movies.delete().map { response =>
          response.error should be(false)
        }
      }
      "drop the actors collection" in {
        actors.delete().map { response =>
          response.error should be(false)
        }
      }
      "drop the actsIn collection" in {
        actsIn.delete().map { response =>
          response.error should be(false)
        }
      }
      "drop the graph" in {
        graph.delete(dropCollections = true).map { response =>
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

case class ActsIn(`type`: String,
                  _from: String,
                  _to: String,
                  roles: List[String],
                  year: Int,
                  _id: Option[String] = None,
                  _key: Option[String] = None,
                  _rev: Option[String] = None) extends Edge

case class MovieAndActors(title: String, released: Int, tagline: String, _key: String, _id: String, _rev: String, actors: List[Actor])