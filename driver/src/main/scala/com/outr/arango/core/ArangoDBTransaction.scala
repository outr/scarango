package com.outr.arango.core

import cats.effect.IO
import com.arangodb
import com.arangodb.entity.StreamTransactionStatus
import com.arangodb.model.StreamTransactionOptions
import com.outr.arango.util.Helpers._

import scala.concurrent.duration.FiniteDuration
import scala.jdk.CollectionConverters._

class ArangoDBTransaction[Collection](db: arangodb.ArangoDatabase, c2Name: Collection => String) {
  def apply[Return](allowImplicit: Boolean = true,
                    lockTimeout: Option[FiniteDuration] = None,
                    waitForSync: Boolean = false,
                    maxTransactionSize: Option[Long] = None,
                    locks: List[(Collection, TransactionLock)] = Nil)
                   (f: StreamTransaction => IO[Return]): IO[Return] = {
    begin(allowImplicit, lockTimeout, waitForSync, maxTransactionSize, locks)
      .flatMap { transaction =>
        f(transaction).attempt.flatMap {
          case Left(throwable) => abort(transaction).map(_ => throw throwable)
          case Right(r) => commit(transaction).map(_ => r)
        }
      }
  }

  def begin(allowImplicit: Boolean = true,
            lockTimeout: Option[FiniteDuration] = None,
            waitForSync: Boolean = false,
            maxTransactionSize: Option[Long] = None,
            locks: List[(Collection, TransactionLock)] = Nil): IO[StreamTransaction] = {
    val options = new StreamTransactionOptions
    lockTimeout.foreach(d => options.lockTimeout(d.toSeconds.toInt))
    options.allowImplicit(allowImplicit)
    options.waitForSync(waitForSync)
    maxTransactionSize.foreach(options.maxTransactionSize(_))
    val read = locks.collect {
      case (collection, lock) if lock == TransactionLock.Read => c2Name(collection)
    }
    if (read.nonEmpty) {
      options.readCollections(read: _*)
    }
    val write = locks.collect {
      case (collection, lock) if lock == TransactionLock.Write => c2Name(collection)
    }
    if (write.nonEmpty) {
      options.writeCollections(write: _*)
    }
    val exclusive = locks.collect {
      case (collection, lock) if lock == TransactionLock.Exclusive => c2Name(collection)
    }
    if (exclusive.nonEmpty) {
      options.exclusiveCollections(exclusive: _*)
    }
    io(db.beginStreamTransaction(options))
      .map(entity => StreamTransaction(entity.getId))
  }

  private def t2Status(status: StreamTransactionStatus): TransactionStatus = status match {
    case StreamTransactionStatus.running => TransactionStatus.Running
    case StreamTransactionStatus.committed => TransactionStatus.Committed
    case StreamTransactionStatus.aborted => TransactionStatus.Aborted
  }

  def status(transaction: StreamTransaction): IO[TransactionStatus] = io(db.getStreamTransaction(transaction.id))
    .map(e => t2Status(e.getStatus))

  def abort(transaction: StreamTransaction): IO[TransactionStatus] = io(db.abortStreamTransaction(transaction.id))
    .map(e => t2Status(e.getStatus))

  def commit(transaction: StreamTransaction): IO[TransactionStatus] = io(db.commitStreamTransaction(transaction.id))
    .map(e => t2Status(e.getStatus))

  def toList: IO[List[(StreamTransaction, TransactionStatus)]] = io(db.getStreamTransactions)
    .map(_.asScala.toList.map { entity =>
      (StreamTransaction(entity.getId), t2Status(entity.getState))
    })
}
