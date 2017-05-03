package com.outr.arango.managed

import com.outr.arango.Value

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Adds support to the Graph to manage database upgrades and versioning.
  *
  * Overrides `Graph.init` to run upgrades immediately after other initialization occurs.
  */
trait UpgradeSupport extends Graph {
  private var upgrades = Map.empty[Int, () => Future[Unit]]

  /**
    * A MapCollection must be defined for UpgradeSupport to retain the database version.
    */
  val store: MapCollection

  /**
    * The current version of the database. If no upgrades have been run the version will be 0.
    */
  def version: Future[Int] = store.map.future.intOption(UpgradeSupport.Key).map(_.getOrElse(0))

  /**
    * Determines the latest version available based on the upgrades registered.
    */
  def latestVersion: Int = upgrades.keys.max

  /**
    * Registers an upgrade for a specific version. Version numbers should start at 1 as 0 represents the initial state.
    */
  def register(version: Int)(f: => Future[Unit]): Unit = synchronized {
    upgrades += version -> (() => f)
  }

  /**
    * Runs all registered but un-applied database upgrades to get to the latest version.
    */
  def upgrade(): Future[Unit] = version.flatMap { v =>
    var f = Future.successful(())
    val latest = latestVersion
    (v + 1 to latest).foreach { upgradeTo =>
      val upgrade = upgrades.getOrElse(upgradeTo, throw new RuntimeException(s"Attempting to upgrade to $upgradeTo, but no upgrade found!"))
      f = f.flatMap(_ => upgrade().map(_ => store.map += UpgradeSupport.Key -> Value(upgradeTo)))
    }
    f.foreach { _ =>
      scribe.info(s"Successfully upgraded from $v to $latest.")
    }
    f
  }

  override def init(createGraph: Boolean, createCollections: Boolean): Future[Boolean] = super.init(createGraph, createCollections).flatMap { b =>
    upgrade().map(_ => b)
  }
}

object UpgradeSupport {
  val Key = "DatabaseVersion"
}