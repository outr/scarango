package spec

import com.outr.arango._
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

import scala.collection.mutable.ListBuffer
import scala.io.Source

class GraphSpec extends AsyncWordSpec with Matchers {
  "Graph" should {
    val doImport: Boolean = false
    val doDrop: Boolean = false

    "initialize configuration" in {
      Profig.loadDefaults()
      succeed
    }
    "initialize" in {
      database.init().map { _ =>
        succeed
      }
    }
    "have two collections" in {
      database.collections.map(_.name).toSet should be(Set("backingStore", "airports", "flights"))
    }
    "import sample airport data" in {
      if (doImport) {
        val airports = csvToIterator("airports.csv").map { d =>
          Airport(
            name = d(1),
            city = d(2),
            state = d(3),
            country = d(4),
            lat = d(5).toDouble,
            long = d(6).toDouble,
            vip = d(7).toBoolean,
            _identity = Airport.id(d(0))
          )
        }
        database.airports.batch(airports).map { inserted =>
          inserted should be(3375)
        }
      } else {
        succeed
      }
    }
    "import sample flight data" in {
      if (doImport) {
        val flights = csvToIterator("flights.csv").map { d =>
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
        database.flights.batch(flights).map { inserted =>
          inserted should be(286463)
        }
      } else {
        succeed
      }
    }
    "query VIP airports" in {
      val query =
        aql"""
             FOR airport IN ${database.airports}
             FILTER airport.vip
             RETURN airport
           """
      database.airports.query(query).cursor.map { response =>
        response.result.map(_._identity.value).toSet should be(Set("JFK", "ORD", "LAX", "ATL", "AMA", "SFO", "DFW"))
      }
    }
    "query JFK airport" in {
      val query = aql"RETURN DOCUMENT(${Airport.id("JFK")})"
      database.airports.query(query).one.map { airport =>
        airport.name should be("John F Kennedy Intl")
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
      database.query(query).includeCount.as[AirportName].cursor.map { response =>
        response.count should be(2)
        response.result.toSet should be(Set(AirportName("John F Kennedy Intl"), AirportName("Los Angeles International")))
      }
    }
    "count all the airports" in {
      val query = aql"RETURN COUNT(${database.airports})"
      database.query(query).as[Int].one.map { count =>
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
      database.query(query).as[String].cursor.map { response =>
        response.result.length should be(82)
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
      database.airports.query(query).cursor.map { response =>
        response.result.length should be(82)
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
      database.query(query).as[String].cursor.map { response =>
        response.result should be(List("Bismarck Municipal", "Denver Intl", "John F Kennedy Intl"))
      }
    }
    "query the views and verify one exists" in {
      database.arangoDatabase.views().map { views =>
        views.map(_.name) should contain("flightSearch")
      }
    }
    "drop the database" in {
      if (doDrop) {
        database.drop().map { _ =>
          succeed
        }
      } else {
        succeed
      }
    }
  }

  def csvToIterator(fileName: String): Iterator[Vector[String]] = {
    val source = Source.fromURL(getClass.getClassLoader.getResource(fileName))
    val iterator = source.getLines()
    iterator.next()     // Skip heading
    iterator.map { s =>
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
    }
  }

  object database extends Graph(databaseName = "graphTest") {
    val airports: Collection[Airport] = new Collection[Airport](this, Airport, CollectionType.Document, Nil)
    val flights: Collection[Flight] = new Collection[Flight](this, Flight, CollectionType.Edge, Nil)
    val flightSearch: View[Flight] = new View[Flight]("flightSearch", Nil, flights)
  }

  case class Airport(name: String,
                     city: String,
                     state: String,
                     country: String,
                     lat: Double,
                     long: Double,
                     vip: Boolean,
                     _identity: Id[Airport] = Airport.id()) extends Document[Airport]

  object Airport extends DocumentModel[Airport] {
    val name: Field[String] = Field[String]("name")

    override val collectionName: String = "airports"
    override implicit val serialization: Serialization[Airport] = Serialization.auto[Airport]
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
                    _identity: Id[Flight] = Flight.id()) extends Edge[Flight, Airport, Airport]

  object Flight extends DocumentModel[Flight] {
    override val collectionName: String = "flights"
    override implicit val serialization: Serialization[Flight] = Serialization.auto[Flight]
  }

  case class AirportName(fullName: String)
}