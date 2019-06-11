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
  private var upgrades = Map.empty[Int, Boolean => Future[Unit]]

  /**
    * A MapCollection must be defined for UpgradeSupport to retain the database version.
    */
  val store: MapCollection

  /**
    * The current version of the database. If no upgrades have been run the version will be 0.
    */
  def version: Future[Int] = store.map.future.intOption(UpgradeSupport.Key).map(_.getOrElse(0))

  /**
    * Determines the latest version available based on the upgrades registered. If no upgrades are associated with this
    * Graph, the value will be 0.
    */
  def latestVersion: Int = upgrades.keys match {
    case keys if keys.nonEmpty => keys.max
    case _ => 0
  }

  /**
    * Registers an upgrade for a specific version. Version numbers should start at 1 as 0 represents the initial state.
    *
    * @param runOnNewDatabase if false the upgrade will be skipped if the upgrade is invoked on a new database. This is
    *                         useful for database migration functionality that presumes data in a specific state but is
    *                         not applicable on a new instance. Defaults to true.
    */
  def register(version: Int, runOnNewDatabase: Boolean = true)(f: => Future[Unit]): Unit = synchronized {
    upgrades += version -> ((newDatabase: Boolean) => {
      if (!newDatabase || runOnNewDatabase) {
        f
      } else {
        Future.successful(())
      }
    })
  }

  /**
    * Runs all registered but un-applied database upgrades to get to the latest version.
    */
  def upgrade(): Future[Unit] = version.flatMap { v =>
    var f = Future.successful(())
    val latest = latestVersion
    val newDatabase = v == 0
    if (latest > v) {
      (v + 1 to latest).foreach { upgradeTo =>
        val upgrade = upgrades.getOrElse(upgradeTo, throw new RuntimeException(s"Attempting to upgrade to $upgradeTo, but no upgrade found!"))
        f = f.flatMap(_ => upgrade(newDatabase).map(_ => store.map += UpgradeSupport.Key -> Value.int(upgradeTo)))
      }
      f.foreach { _ =>
        scribe.info(s"Successfully upgraded from $v to $latest.")
      }
    } else {
      scribe.info(s"Current database version $v is the latest.")
    }
    f
  }

  override def init(createDatabase: Boolean = true,
                    createGraph: Boolean = true,
                    createCollections: Boolean = true,
                    force: Boolean = false): Future[Boolean] = {
    super.init(createDatabase, createGraph, createCollections, force).flatMap { b =>
      upgrade().map(_ => b)
    }
  }
}

object UpgradeSupport {
  val Key = "DatabaseVersion"
}