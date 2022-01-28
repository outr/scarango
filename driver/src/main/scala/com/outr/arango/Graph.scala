package com.outr.arango

import cats.effect.IO
import cats.implicits._
import com.outr.arango.collection.{Collection, DocumentCollection, QueryBuilder}
import com.outr.arango.core.{ArangoDB, ArangoDBCollection, ArangoDBConfig, ArangoDBDocuments, ArangoDBServer, ArangoDBTransaction, CollectionInfo, ConsolidationPolicy, SortCompression}
import com.outr.arango.query.{Query, QueryPart, Sort}
import com.outr.arango.upgrade.{CreateDatabase, DatabaseUpgrade}
import com.outr.arango.view.{View, ViewLink}
import fabric._
import fabric.rw._

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class Graph(private[arango] val db: ArangoDB) {
  private val _initialized = new AtomicBoolean(false)

  private var _collections: List[DocumentCollection[_]] = Nil
  private var _views: List[View] = Nil
  private var _stores: List[DatabaseStore] = Nil
  protected def storeCollectionName: String = "backingStore"

  def collections: List[DocumentCollection[_]] = _collections
  def views: List[View] = _views
  def stores: List[DatabaseStore] = _stores

  def this(name: String, server: ArangoDBServer) = {
    this(server.db(name))
  }

  def this(name: String, config: ArangoDBConfig) = {
    this(name, ArangoDBServer(config))
  }

  def this(name: String) = {
    this(name, ArangoDBConfig())
  }

  val store: DatabaseStore = keyStore(storeCollectionName)

  def initialized: Boolean = _initialized.get()

  def init(): IO[Unit] = if (_initialized.compareAndSet(false, true)) {
    for {
      _ <- CreateDatabase.upgrade(this)
      appliedUpgrades <- store[AppliedUpgrades](AppliedUpgrades.key, _ => AppliedUpgrades.empty).map(_.labels)
      upgrades = this.upgrades.filter(u => u.alwaysRun || !appliedUpgrades.contains(u.label))
      _ = if (upgrades.nonEmpty) scribe.info(s"Applying ${upgrades.length} upgrades (${upgrades.map(_.label).mkString(", ")})...")
      _ <- doUpgrades(upgrades, upgrades, stillBlocking = true, appliedUpgrades = Set.empty)
    } yield {
      ()
    }
  } else {
    IO.unit
  }

  protected def initted[Return](f: => Return): Return = {
    assert(initialized, "Database has not been initialized yet")
    f
  }

  def query[T: ReaderWriter](query: Query): QueryBuilder[T] = QueryBuilder[T](this, query, implicitly[ReaderWriter[T]])

  def databaseName: String = db.name

  lazy val transaction = new ArangoDBTransaction[Collection](db.db, _.name)

  def upgrades: List[DatabaseUpgrade] = Nil

  protected def doUpgrades(allUpgrades: List[DatabaseUpgrade],
                           upgrades: List[DatabaseUpgrade],
                           stillBlocking: Boolean,
                           appliedUpgrades: Set[String]): IO[Unit] = if (upgrades.isEmpty) {
    afterStartup(allUpgrades)
  } else {
    val continueBlocking = upgrades.exists(_.blockStartup)
    val upgrade = upgrades.head

    val io = for {
      _ <- upgrade.upgrade(this)
      applied = appliedUpgrades + upgrade.label
      _ <- store(AppliedUpgrades.key) = AppliedUpgrades(applied)
      _ <- doUpgrades(allUpgrades, upgrades.tail, continueBlocking, applied)
    } yield {
      ()
    }

    if (stillBlocking && !continueBlocking) {
      // Break free
      io.unsafeRunAndForget()(cats.effect.unsafe.IORuntime.global)

      IO.unit
    } else {
      io
    }
  }

  protected def afterStartup(upgrades: List[DatabaseUpgrade]): IO[Unit] = if (upgrades.isEmpty) {
    scribe.info("Upgrades completed successfully")
    IO.unit
  } else {
    val upgrade = upgrades.head
    upgrade.afterStartup(this).flatMap { _ =>
      afterStartup(upgrades.tail)
    }
  }

  def truncate(): IO[Unit] = collections.map(_.collection.truncate()).sequence.map(_ => ())

  def vertex[D <: Document[D]](model: DocumentModel[D]): DocumentCollection[D] =
    collection(model, CollectionType.Vertex)
  def edge[D <: Document[D]](model: DocumentModel[D]): DocumentCollection[D] =
    collection(model, CollectionType.Edge)

  def collection[D <: Document[D]](model: DocumentModel[D], `type`: CollectionType): DocumentCollection[D] = synchronized {
    val c = new DocumentCollection[D](this, db.collection(model.collectionName), model, `type`)
    _collections = _collections ::: List(c)
    c
  }

  def view(name: String,
           links: List[ViewLink],
           primarySort: List[Sort] = Nil,
           primarySortCompression: SortCompression = SortCompression.LZ4,
           consolidationInterval: FiniteDuration = 1.second,
           commitInterval: FiniteDuration = 1.second,
           cleanupIntervalStep: Int = 2,
           consolidationPolicy: ConsolidationPolicy = ConsolidationPolicy.BytesAccum()): View = synchronized {
    val view = db.view(name, links, primarySort, primarySortCompression, consolidationInterval, commitInterval, cleanupIntervalStep, consolidationPolicy)
    _views = _views ::: List(view)
    view
  }

  def keyStore(collectionName: String): DatabaseStore = synchronized {
    val s = DatabaseStore(db.collection(collectionName))
    _stores = _stores ::: List(s)
    s
  }

  def drop(): IO[Boolean] = db.drop()

  case class AppliedUpgrades(labels: Set[String])

  object AppliedUpgrades {
    implicit val rw: ReaderWriter[AppliedUpgrades] = ccRW

    val key: String = "appliedUpgrades"

    val empty: AppliedUpgrades = AppliedUpgrades(Set.empty)
  }
}