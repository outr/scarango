package com.outr.arango.upgrade

import cats.effect.IO
import com.outr.arango.Graph

trait DatabaseUpgrade {
  def label: String = getClass.getSimpleName.replace("$", "")
  def applyToNew: Boolean
  def blockStartup: Boolean
  def alwaysRun: Boolean = false

  def upgrade(graph: Graph): IO[Unit]

  def afterStartup(graph: Graph): IO[Unit] = IO.unit
}