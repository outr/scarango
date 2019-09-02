package com.outr.arango.transaction

import com.outr.arango.Graph
import io.youi.net._

import scala.concurrent.Future
import scala.language.experimental.macros

case class Transaction(id: String) {

}

object Transaction {
  // TODO: can't create transaction until we have an intance of Graph (chicken or egg)
  def create(graph: Graph): Future[Transaction] = {
    val client = graph
      .arangoDB
      .client
      .path(path"/_api/transaction/begin", append = true)
    scribe.info(s"Path: ${client.path}")
    ???
  }

  def apply[G <: Graph, Return](transaction: G => Future[Return]): Future[Return] = macro TransactionMacros.simple[G, Return]
}