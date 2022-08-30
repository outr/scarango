package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoDatabaseAsync
import com.arangodb.entity.arangosearch.{ArangoSearchCompression, CollectionLink, FieldLink, PrimarySort, StoreValuesType}
import com.arangodb.model.arangosearch.ArangoSearchCreateOptions
import com.outr.arango.query.{Query, Sort, SortDirection}
import com.outr.arango.util.Helpers._
import com.outr.arango.view.{View, ViewLink}
import fabric._

import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

class ArangoDB(val server: ArangoDBServer, private[arango] val db: ArangoDatabaseAsync) {
  def name: String = db.dbName().get()

  def create(): IO[Boolean] = db.create().toIO.map(_.booleanValue())

  def exists(): IO[Boolean] = db.exists().toIO.map(_.booleanValue())

  def drop(): IO[Boolean] = db.drop().toIO.map(_.booleanValue())

  lazy val transaction = new ArangoDBTransaction[ArangoDBCollection](db, _.name)

  object query {
    def parse(query: Query): IO[AQLParseResult] = {
      db.parseQuery(query.string).toIO.map(aqlParseEntityConversion)
    }

    def iterator(query: Query): IO[Iterator[Json]] = {
      val bindVars: java.util.Map[String, AnyRef] = query.variables.map {
        case (key, value) => key -> value2AnyRef(value)
      }.asJava

      db
        .query(query.string, bindVars, classOf[Json])
        .toIO
        .attempt
        .map {
          case Left(throwable) =>
            scribe.error(s"An error occurred executing a query: $query", throwable)
            throw throwable
          case Right(c) =>
            val cursor: java.util.Iterator[Json] = c
            cursor.asScala
        }
    }

    def apply(query: Query): fs2.Stream[IO, Json] = fs2.Stream.force(iterator(query).flatMap { iterator =>
      IO(fs2.Stream.fromIterator[IO](iterator, 512))
    })

    def execute(query: Query): IO[Unit] = apply(query).compile.drain
  }

  def collection(name: String): ArangoDBCollection = new ArangoDBCollection(db.collection(name))
  def view(name: String,
           links: List[ViewLink],
           primarySort: List[Sort] = Nil,
           primarySortCompression: SortCompression = SortCompression.LZ4,
           consolidationInterval: FiniteDuration = 1.second,
           commitInterval: FiniteDuration = 1.second,
           cleanupIntervalStep: Int = 2,
           consolidationPolicy: ConsolidationPolicy = ConsolidationPolicy.BytesAccum()): View = {
    val o = new ArangoSearchCreateOptions
    o.consolidationIntervalMsec(consolidationInterval.toMillis)
    o.commitIntervalMsec(commitInterval.toMillis)
    o.cleanupIntervalStep(cleanupIntervalStep.toLong)
    import com.arangodb.entity.{arangosearch => as}
    val cp = consolidationPolicy match {
      case ConsolidationPolicy.BytesAccum(threshold) => as
        .ConsolidationPolicy
        .of(as.ConsolidationType.BYTES_ACCUM)
        .threshold(threshold)
      case ConsolidationPolicy.Tier(segmentThreshold) => as
        .ConsolidationPolicy
        .of(as.ConsolidationType.TIER)
        .segmentThreshold(segmentThreshold)
    }
    o.consolidationPolicy(cp)
    val cls = links.map { l =>
      val fields = l.fields map {
        case (field, analyzers) =>
          val fl = FieldLink.on(field.fieldName)
          fl.analyzers(analyzers.map(_.name): _*)
          // TODO: Support other FieldLink options
          fl
      }
      CollectionLink
        .on(l.collection.name)
        .analyzers(l.analyzers.map(_.name): _*)
        .fields(fields: _*)
        .includeAllFields(l.includeAllFields)
        .trackListPositions(l.trackListPositions)
        .storeValues(if (l.storeValues) StoreValuesType.ID else StoreValuesType.NONE)
        // TODO: Support inBackground - currently not supported in Java driver

    }
    o.link(cls: _*)
    val ps = primarySort.map { sort =>
      PrimarySort.on(sort.field.fieldName).ascending(sort.direction == SortDirection.ASC)
    }
    o.primarySort(ps: _*)
    o.primarySortCompression(primarySortCompression match {
      case SortCompression.LZ4 => ArangoSearchCompression.lz4
      case SortCompression.None => ArangoSearchCompression.none
    })
    // TODO: Support storedvalues
//    o.storedValues()
    new View(this, name, o)
  }

  def shutdown(): Unit = db.arango().shutdown()
}