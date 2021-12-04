package com.outr.arango.upgrade

import com.outr.arango.Graph

import scala.concurrent.Future

trait DatabaseUpgrade {
  def label: String = getClass.getSimpleName.replace("$", "")
  def applyToNew: Boolean
  def blockStartup: Boolean
  def alwaysRun: Boolean = false

  def upgrade(graph: Graph): Future[Unit]

  def afterStartup(graph: Graph): Future[Unit] = Future.successful(())
}