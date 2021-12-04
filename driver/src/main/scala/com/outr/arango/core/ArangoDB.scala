package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.{ArangoDatabaseAsync, ArangoViewAsync}
import com.arangodb.entity.arangosearch.{ArangoSearchCompression, CollectionLink, FieldLink, PrimarySort, StoreValuesType}
import com.arangodb.entity.{StreamTransactionStatus, ViewType}
import com.arangodb.model.StreamTransactionOptions
import com.arangodb.model.arangosearch.ArangoSearchCreateOptions
import com.outr.arango._
import com.outr.arango.query.{Query, QueryPart, Sort, SortDirection}
import com.outr.arango.util.Helpers._
import fabric._

import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.util.Try

case class StreamTransaction(id: String)

sealed trait TransactionLock

object TransactionLock {
  case object Read extends TransactionLock
  case object Write extends TransactionLock
  case object Exclusive extends TransactionLock
}

sealed trait TransactionStatus

object TransactionStatus {
  case object Running extends TransactionStatus
  case object Committed extends TransactionStatus
  case object Aborted extends TransactionStatus
}

class ArangoDB(private[arango] val db: ArangoDatabaseAsync) {
  def name: String = db.name()

  def create(): IO[Boolean] = db.create().toIO.map(_.booleanValue())

  def exists(): IO[Boolean] = db.exists().toIO.map(_.booleanValue())

  def drop(): IO[Boolean] = db.drop().toIO.map(_.booleanValue())

  object transaction {
    def begin(allowImplicit: Boolean = true,
              lockTimeout: Option[FiniteDuration] = None,
              waitForSync: Boolean = false,
              maxTransactionSize: Option[Long] = None,
              locks: List[(ArangoDBCollection, TransactionLock)]): IO[StreamTransaction] = {
      val options = new StreamTransactionOptions
      lockTimeout.foreach(d => options.lockTimeout(d.toSeconds.toInt))
      options.allowImplicit(allowImplicit)
      options.waitForSync(waitForSync)
      maxTransactionSize.foreach(options.maxTransactionSize(_))
      val read = locks.collect {
        case (collection, lock) if lock == TransactionLock.Read => collection.collection.name()
      }
      if (read.nonEmpty) {
        options.readCollections(read: _*)
      }
      val write = locks.collect {
        case (collection, lock) if lock == TransactionLock.Write => collection.collection.name()
      }
      if (write.nonEmpty) {
        options.writeCollections(write: _*)
      }
      val exclusive = locks.collect {
        case (collection, lock) if lock == TransactionLock.Exclusive => collection.collection.name()
      }
      if (exclusive.nonEmpty) {
        options.exclusiveCollections(exclusive: _*)
      }
      db.beginStreamTransaction(options)
        .toIO
        .map(entity => StreamTransaction(entity.getId))
    }

    private def t2Status(status: StreamTransactionStatus): TransactionStatus = status match {
      case StreamTransactionStatus.running => TransactionStatus.Running
      case StreamTransactionStatus.committed => TransactionStatus.Committed
      case StreamTransactionStatus.aborted => TransactionStatus.Aborted
    }

    def status(transaction: StreamTransaction): IO[TransactionStatus] = db.getStreamTransaction(transaction.id)
      .toIO
      .map(e => t2Status(e.getStatus))

    def abort(transaction: StreamTransaction): IO[TransactionStatus] = db.abortStreamTransaction(transaction.id)
      .toIO
      .map(e => t2Status(e.getStatus))

    def commit(transaction: StreamTransaction): IO[TransactionStatus] = db.commitStreamTransaction(transaction.id)
      .toIO
      .map(e => t2Status(e.getStatus))

    def all: IO[List[(StreamTransaction, TransactionStatus)]] = db.getStreamTransactions
      .toIO
      .map(_.asScala.toList.map { entity =>
        (StreamTransaction(entity.getId), t2Status(entity.getStatus))
      })
  }

  object query {
    def parse(query: Query): IO[AQLParseResult] = {
      db.parseQuery(query.string).toIO.map(aqlParseEntityConversion)
    }

    def apply(query: Query): fs2.Stream[IO, Value] = {
      val bindVars: java.util.Map[String, AnyRef] = query.variables.map {
        case (key, value) => key -> value2AnyRef(value)
      }.asJava

      fs2.Stream.force(db.query(query.string, bindVars, classOf[String]).toIO.map { c =>
        // TODO: Consider c.stream() instead
        val cursor: java.util.Iterator[String] = c
        val iterator: Iterator[String] = cursor.asScala
        fs2.Stream.fromBlockingIterator[IO](iterator, 512)
      }).map(s => Try(fabric.parse.Json.parse(s)).getOrElse(str(s)))    // TODO: re-evaluate basic strings
    }
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
    o.cleanupIntervalStep(cleanupIntervalStep)
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
        // TODO: Support inBackground

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
}

sealed trait SortCompression

object SortCompression {
  case object LZ4 extends SortCompression
  case object None extends SortCompression
}

case class ViewLink(collection: Collection,
                    analyzers: List[Analyzer] = List(Analyzer.Identity),
                    fields: List[(Field[_], List[Analyzer])] = Nil,
                    includeAllFields: Boolean = false,
                    trackListPositions: Boolean = false,
                    storeValues: Boolean = false,
                    inBackground: Boolean = false)

sealed trait ConsolidationPolicy

object ConsolidationPolicy {
  case class BytesAccum(threshold: Double = 0.85) extends ConsolidationPolicy
  case class Tier(segmentThreshold: Long = 300) extends ConsolidationPolicy
}

class View(db: ArangoDB, val name: String, options: ArangoSearchCreateOptions) extends QueryPart.Support {
  def dbName: String = db.name

  private val view = db.db.view(name)

  def create(): IO[ViewInfo] = db.db.createArangoSearch(name, options)
    .toIO
    .map { entity =>
      ViewInfo(entity.getId, entity.getName)
    }

  def exists(): IO[Boolean] = view.exists().toIO.map(_.booleanValue())

  def drop(): IO[Unit] = view.drop().toIO.map(_ => ())

  override def toQueryPart: QueryPart = QueryPart.Static(name)
}

case class ViewInfo(id: String, name: String)