package com.outr.arango.upgrade

import com.outr.arango.transaction.Transaction
import com.outr.arango.{Collection, Graph}

import scala.concurrent.Future
import scribe.Execution.global

trait TransactionalDatabaseUpgrade extends DatabaseUpgrade {
  def write: List[Collection[_]]
  def read: List[Collection[_]]
  def exclusive: List[Collection[_]]
  def waitForSync: Boolean = true
  def allowImplicit: Boolean = true

  override final def upgrade(graph: Graph): Future[Unit] = graph.transaction(
    write = write,
    read = read,
    exclusive = exclusive,
    waitForSync = waitForSync,
    allowImplicit = allowImplicit
  ).flatMap { transaction =>
    upgrade(graph, transaction).flatMap { _ =>
      transaction.commit()
    }.recoverWith {
      case t: Throwable => {
        scribe.error(s"Error while executing transactional upgrade $label. Rolling back...", t)
        transaction.abort()
      }
    }
  }

  def upgrade(graph: Graph, transaction: Transaction): Future[Unit]
}