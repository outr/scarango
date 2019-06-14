package spec

import com.outr.arango.{Collection, Document, DocumentModel, Edge, Graph, Id, Serialization}
import org.scalatest.{AsyncWordSpec, Matchers}
import profig.Profig

class GraphSpec extends AsyncWordSpec with Matchers {
  "Graph" should {
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
      database.collections.map(_.name) should be(List("airports", "flights"))
    }
  }

  object database extends Graph {
    val airports: Collection[Airport] = new Collection[Airport](this, Airport)
    val flights: Collection[Flight] = new Collection[Flight](this, Flight)
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
}