package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoDatabaseAsync
import com.arangodb.entity.StreamTransactionStatus
import com.arangodb.model.StreamTransactionOptions
import com.outr.arango._
import com.outr.arango.util.Helpers._
import fabric._

import scala.concurrent.duration.FiniteDuration
import scala.jdk.CollectionConverters._

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

class ArangoDB(db: ArangoDatabaseAsync) {
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
      }).map(fabric.parse.Json.parse)
    }
  }

  def collection(name: String): ArangoDBCollection = new ArangoDBCollection(db.collection(name))
}