package spec

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.outr.arango._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.core._
import com.outr.arango.query._
import com.outr.arango.upgrade.DatabaseUpgrade
import com.outr.arango.view.{View, ViewLink}
import fabric.rw.{ReaderWriter, ccRW}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import profig.Profig

import scala.collection.mutable.ListBuffer
import scala.io.Source

class GraphSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {
  "Graph" should {
    val doDrop: Boolean = false

    "initialize configuration" in {
      Profig.initConfiguration()
      succeed
    }
    "initialize database" in {
      database.init().map { _ =>
        succeed
      }
    }
    "have two collections" in {
      database.collections.map(_.name).toSet should be(Set("airports", "flights"))
    }
    "query VIP airports" in {
      val query =
        aql"""
             FOR airport IN ${database.airports}
             FILTER airport.vip
             RETURN airport
           """
      database.airports.query(query).compile.toList.asserting { results =>
        results.map(_._id.value).toSet should be(Set("JFK", "ORD", "LAX", "ATL", "AMA", "SFO", "DFW"))
      }
    }
    "query JFK airport" in {
      val query = aql"RETURN DOCUMENT(${Airport.id("JFK")})"
      database.airports.query(query).compile.toList.map { airports =>
        airports.length should be(1)
        airports.head.name should be("John F Kennedy Intl")
      }
    }
    "query just the airport's full name" in {
      val keys = List("JFK", "LAX")
      val query =
        aql"""
             FOR a IN ${database.airports}
             FILTER a._key IN $keys
             RETURN {fullName: a.name}
           """
      database.queryAs[AirportName](query).compile.toList.map { airportNames =>
        airportNames.length should be(2)
        airportNames.toSet should be(Set(AirportName("John F Kennedy Intl"), AirportName("Los Angeles International")))
      }
    }
    "count all the airports" in {
      val query = aql"RETURN COUNT(${database.airports})"
      database.queryAs[Int](query).compile.lastOrError.map { count =>
        count should be(3375)
      }
    }
    "get all airport names reachable directly from LAX following edges" in {
      val lax = Airport.id("LAX")
      val query =
        aql"""
             FOR airport IN 1..1 OUTBOUND $lax ${database.flights}
             RETURN DISTINCT airport.name
           """
      database.queryAs[String](query).compile.toList.map { response =>
        response.length should be(82)
      }
    }
    "traverse all airports reachable from LAX" in {
      val lax = Airport.id("LAX")
      val query =
        aql"""
             FOR airport IN OUTBOUND $lax ${database.flights}
             OPTIONS { bfs: true, uniqueVertices: 'global' }
             RETURN airport
           """
      database.airports.query(query).compile.toList.map { response =>
        response.length should be(82)
      }
    }
    "find the shortest path between BIS and JFK" in {
      val bis = Airport.id("BIS")
      val jfk = Airport.id("JFK")
      val query =
        aql"""
             FOR v IN OUTBOUND
             SHORTEST_PATH $bis TO $jfk ${database.flights}
             RETURN v.${Airport.name}
           """
      database.queryAs[String](query).compile.toList.map { response =>
        response should be(List("Bismarck Municipal", "Denver Intl", "John F Kennedy Intl"))
      }
    }
    "query the views and verify one exists" in {
      database.views.map(_.name) should be(List("airportSearch"))
    }
    "search the view" in {
      val word = "navajo"
      val query =
        aql"""
             FOR a IN ${database.airportSearch} SEARCH PHRASE(a.name, $word, ${Analyzer.TextEnglish}) RETURN a
           """
      database.airports.query(query).compile.toList.map { response =>
        val names = response.map(_.name)
        names should be(List("Navajo State Park"))
      }
    }
    "drop the database" in {
      if (doDrop) {
        database.drop().map { success =>
          success should be(true)
        }
      } else {
        succeed
      }
    }
  }

  def csv2Stream(fileName: String): fs2.Stream[IO, Vector[String]] = {
    val source = Source.fromURL(getClass.getClassLoader.getResource(fileName))
    val iterator = source.getLines()
    iterator.next()     // Skip heading
    fs2.Stream.fromIterator[IO](iterator.map { s =>
      var open = false
      val entries = ListBuffer.empty[String]
      val b = new StringBuilder
      s.foreach { c =>
        if (c == '"') {
          open = !open
        } else if (c == ',' && !open) {
          if (b.nonEmpty) {
            entries += b.toString().trim
            b.clear()
          }
        } else {
          b.append(c)
        }
      }
      if (b.nonEmpty) entries += b.toString().trim
      entries.toVector
    }, 1000)
  }

  object database extends Graph(name = "graphTest") {
    val airports: DocumentCollection[Airport] = vertex[Airport](Airport)
    val flights: DocumentCollection[Flight] = edge[Flight](Flight)

    val airportSearch: View = view(
      name = "airportSearch",
      links = List(ViewLink(airports, fields = List(
        Airport.name -> List(Analyzer.TextEnglish)
      )))
    )

    override def upgrades: List[DatabaseUpgrade] = List(
      DataImportUpgrade
    )
  }

  case class Airport(name: String,
                     city: String,
                     state: String,
                     country: String,
                     lat: Double,
                     long: Double,
                     vip: Boolean,
                     _id: Id[Airport] = Airport.id()) extends Document[Airport]

  object Airport extends DocumentModel[Airport] {
    override implicit val rw: ReaderWriter[Airport] = ccRW

    val name: Field[String] = Field[String]("name")

    override def indexes: List[Index] = Nil

    override val collectionName: String = "airports"
  }

  case class Flight(_from: Id[Airport],
                    _to: Id[Airport],
                    year: Int,
                    month: Int,
                    day: Int,
                    dayOfWeek: Int,
                    depTime: Int,
                    arrTime: Int,
                    depTimeUTC: String,
                    arrTimeUTC: String,
                    uniqueCarrier: String,
                    flightNum: Int,
                    tailNum: String,
                    distance: Int,
                    _id: Id[Flight] = Flight.id()) extends Edge[Flight, Airport, Airport]

  object Flight extends DocumentModel[Flight] {
    override implicit val rw: ReaderWriter[Flight] = ccRW

    override def indexes: List[Index] = Nil

    override val collectionName: String = "flights"
  }

  case class AirportName(fullName: String)

  object AirportName {
    implicit val rw: ReaderWriter[AirportName] = ccRW
  }

  object DataImportUpgrade extends DatabaseUpgrade {
    override def applyToNew: Boolean = true
    override def blockStartup: Boolean = true

    override def upgrade(graph: Graph): IO[Unit] = for {
      insertedAirports <- {
        val airports = csv2Stream("airports.csv").map { d =>
          Airport(
            name = d(1),
            city = d(2),
            state = d(3),
            country = d(4),
            lat = d(5).toDouble,
            long = d(6).toDouble,
            vip = d(7).toBoolean,
            _id = Airport.id(d(0))
          )
        }
        database.airports.document.stream.insert(airports)
      }
      _ = insertedAirports should be(3375)
      insertedFlights <- {
        val flights = csv2Stream("flights.csv").map { d =>
          Flight(
            _from = Airport.id(d(0)),
            _to = Airport.id(d(1)),
            year = d(2).toInt,
            month = d(3).toInt,
            day = d(4).toInt,
            dayOfWeek = d(5).toInt,
            depTime = d(6).toInt,
            arrTime = d(7).toInt,
            depTimeUTC = d(8),
            arrTimeUTC = d(9),
            uniqueCarrier = d(10),
            flightNum = d(11).toInt,
            tailNum = d(12),
            distance = d(13).toInt
          )
        }
        database.flights.document.stream.insert(flights)
      }
      _ = insertedFlights should be(286463)
    } yield {
      ()
    }
  }
}