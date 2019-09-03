package com.outr.arango.transaction

import com.outr.arango.Graph
import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}

import scala.concurrent.{ExecutionContext, Future}

case class Transaction(id: String, status: TransactionStatus) {
  private[arango] var graph: Option[Graph] = None

  def withGraph(option: Option[Graph]): Transaction = {
    graph = option
    this
  }

  def checkStatus()(implicit ec: ExecutionContext): Future[Transaction] = {
    val g = graph.getOrElse(throw new RuntimeException("Graph not included!"))
    g.arangoDatabase.transactionStatus(id).map(_.withGraph(graph))
  }

  def commit()(implicit ec: ExecutionContext): Future[Unit] = {
    val g = graph.getOrElse(throw new RuntimeException("Graph not included!"))
    g.arangoDatabase.transactionCommit(id).map { t =>
      t.status match {
        case TransactionStatus.Running => throw new RuntimeException("Commit failed, transaction still running!")
        case TransactionStatus.Committed => ()
        case TransactionStatus.Aborted => throw new RuntimeException("Commit failed, transaction aborted!")
      }
    }
  }

  def abort()(implicit ec: ExecutionContext): Future[Unit] = {
    val g = graph.getOrElse(throw new RuntimeException("Graph not included!"))
    g.arangoDatabase.transactionAbort(id).map { t =>
      t.status match {
        case TransactionStatus.Running => throw new RuntimeException("Abort failed, transaction still running!")
        case TransactionStatus.Committed => throw new RuntimeException("Abort failed, transaction committed!")
        case TransactionStatus.Aborted => ()
      }
    }
  }
}