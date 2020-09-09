package com.outr.arango

import java.util.concurrent.atomic.AtomicBoolean

import com.outr.arango.api.model.ArangoLinkFieldProperties
import com.outr.arango.model.ArangoCode
import com.outr.arango.transaction.Transaction
import com.outr.arango.upgrade.{CreateDatabase, DatabaseUpgrade}
import io.circe.Json
import io.youi.client.HttpClient
import io.youi.net.URL

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.language.experimental.macros

/**
  * Graph represents a graph database
  *
  * TODO: This is currently an anonymous graph without support for named graphs. Support should be added for named.
  */
class Graph(val databaseName: String = ArangoDB.config.db,
            baseURL: URL = ArangoDB.config.url,
            credentials: Option[Credentials] = ArangoDB.credentials,
            httpClient: HttpClient = HttpClient) {
  private var _collections: List[Collection[_]] = Nil
  private var _views: List[View[_]] = Nil
  private var _initializations: List[() => Future[Unit]] = Nil
  private val _initialized = new AtomicBoolean(false)
  private var versions = ListBuffer.empty[DatabaseUpgrade]

  lazy val arangoDB: ArangoDB = new ArangoDB(databaseName, baseURL, credentials, httpClient)
  lazy val arangoDatabase: ArangoDatabase = arangoDB.api.db(databaseName)
  lazy val backingStore: DocumentCollection[BackingStore] = new DocumentCollection[BackingStore](this, BackingStore, CollectionType.Document, Nil, None)

  def wal: ArangoWriteAheadLog = arangoDatabase.wal

  def transaction(write: List[Collection[_]] = Nil,
                  read: List[Collection[_]] = Nil,
                  exclusive: List[Collection[_]] = Nil,
                  waitForSync: Boolean = false,
                  allowImplicit: Boolean = false,
                  maxTransactionSize: Long = -1L)
                 (implicit ec: ExecutionContext): Future[Transaction] = arangoDatabase.transactionCreate(
    writeCollections = write.map(_.name),
    readCollections = read.map(_.name),
    exclusiveCollections = exclusive.map(_.name),
    waitForSync = waitForSync,
    allowImplicit = allowImplicit,
    maxTransactionSize = maxTransactionSize
  ).map(_.withGraph(Some(this)))

  private lazy val databaseVersion: DatabaseStore[DatabaseVersion] = DatabaseStore[DatabaseVersion](
    key = "databaseVersion",
    graph = Graph.this,
    serialization = Serialization.auto[DatabaseVersion]
  )

  register(CreateDatabase)

  def query(query: Query, transaction: Option[Transaction] = None): QueryBuilder[Json] = {
    arangoDatabase.query(query, transaction.map(_.id))
  }

  def collections: List[Collection[_]] = _collections
  def views: List[View[_]] = _views
  def initialized: Boolean = _initialized.get()

  def store[T](key: String): DatabaseStore[T] = macro GraphMacros.store[T]

  def vertex[D <: Document[D]]: DocumentCollection[D] = macro GraphMacros.vertex[D]
  def edge[D <: Document[D]]: DocumentCollection[D] = macro GraphMacros.edge[D]
  def cached[D <: Document[D]](collection: Collection[D]): CachedCollection[D] = new CachedCollection[D](collection)
  def view[D <: Document[D]](name: String,
                             collection: Collection[D],
                             includeAllFields: Boolean,
                             analyzers: List[Analyzer],
                             fields: (Field[_], List[Analyzer])*): View[D] = {
    val fieldsMap: Map[Field[_], ArangoLinkFieldProperties] = Map(fields.map {
      case (f, a) => f -> ArangoLinkFieldProperties(a)
    }: _*)
    new View[D](name, includeAllFields, fieldsMap, collection, analyzers)
  }

  def register(upgrades: DatabaseUpgrade*): Unit = synchronized {
    assert(!initialized, "Database is already initialized. Cannot register upgrades after initialization.")
    upgrades.foreach { upgrade =>
      if (!versions.contains(upgrade)) {
        versions += upgrade
      }
    }
    ()
  }

  def init()(implicit ec: ExecutionContext): Future[Unit] = scribe.async {
    if (_initialized.compareAndSet(false, true)) {
      (for {
        // Initialize the database
        state <- arangoDB.init()
        // Verify it initialized successfully
        _ = assert(state.isInstanceOf[DatabaseState.Initialized], s"ArangoDB failed to initialize with $state")
        // Execute upgrades
        upgrades <- doUpgrades
        // Load cached collections
        _ <- Future.sequence(collections.map {
          case cc: CachedCollection[_] => cc.refresh()
          case _ => Future.successful(())
        })
        // Load initialize tasks
        _ <- Future.sequence(_initializations.map(_()))
        // Execute afterStartup for previously executed upgrades
        _ = upgrades.foreach { upgrade =>
          upgrade.afterStartup(this).failed.foreach { t =>
            scribe.error(s"After Startup failed for ${upgrade.label}", t)
          }
        }
      } yield {
        ()
      }).recover {
        case t: Throwable => {
          arangoDB._state := DatabaseState.Error(t)
          throw t
        }
      }
    } else {
      Future.successful(())
    }
  }

  def truncate()(implicit ec: ExecutionContext): Future[Unit] = scribe.async {
    for {
      _ <- Future.sequence(collections.flatMap {
        case c: WritableCollection[_] => Some(c.truncate())
        case _ => None
      })
    } yield {
      ()
    }
  }

  def drop()(implicit ec: ExecutionContext): Future[Unit] = scribe.async {
    arangoDatabase.drop().map(_ => ())
  }

  private def version(implicit ec: ExecutionContext): Future[DatabaseVersion] = databaseVersion(DatabaseVersion())
    .recover {
      case exc: ArangoException if exc.error.errorCode == ArangoCode.ArangoDatabaseNotFound => DatabaseVersion()      // Database doesn't exist yet
      case exc: ArangoException if exc.error.errorCode == ArangoCode.ArangoCollectionNotFound => DatabaseVersion()    // Collection doesn't exist yet
    }

  private def doUpgrades(implicit ec: ExecutionContext): Future[List[DatabaseUpgrade]] = {
    version.flatMap { version =>
      val upgrades = versions.toList.filterNot(v => version.upgrades.contains(v.label) && !v.alwaysRun)
      upgrade(version, upgrades, version.upgrades.isEmpty).map(_ => upgrades)
    }
  }

  private def upgrade(version: DatabaseVersion,
                      upgrades: List[DatabaseUpgrade],
                      newDatabase: Boolean,
                      currentlyBlocking: Boolean = true)
                     (implicit ec: ExecutionContext): Future[Unit] = scribe.async {
    val blocking = upgrades.exists(_.blockStartup)
    val future = upgrades.headOption match {
      case Some(u) => if (!newDatabase || u.applyToNew) {
        scribe.info(s"Upgrading with database upgrade: ${u.label} (${upgrades.length - 1} upgrades left)...")
        u.upgrade(this).flatMap { _ =>
          val versionUpdated = version.copy(upgrades = version.upgrades + u.label)
          databaseVersion.set(versionUpdated).flatMap { _ =>
            scribe.info(s"Completed database upgrade: ${u.label} successfully")
            upgrade(versionUpdated, upgrades.tail, newDatabase, blocking)
          }
        }
      } else {
        scribe.info(s"Skipping database upgrade: ${u.label} as it doesn't apply to new database")
        val versionUpdated = version.copy(upgrades = version.upgrades + u.label)
        databaseVersion.set(versionUpdated).flatMap { _ =>
          upgrade(versionUpdated, upgrades.tail, newDatabase, blocking)
        }
      }
      case None => Future.successful(())
    }

    if (currentlyBlocking && !blocking && upgrades.nonEmpty) {
      scribe.info("Additional upgrades do not require blocking. Allowing application to start...")
      future.failed.map { throwable =>
        scribe.error("Database upgrade failure", throwable)
      }
      Future.successful(())
    } else {
      future
    }
  }

  private[arango] def add[D <: Document[D]](collection: Collection[D]): Unit = synchronized {
    _collections = _collections.filterNot(_.name == collection.name) ::: List(collection)
  }

  private[arango] def add[D <: Document[D]](view: View[D]): Unit = synchronized {
    _views = _views ::: List(view)
  }

  private[arango] def add(initialization: () => Future[Unit]): Unit = synchronized {
    _initializations = _initializations ::: List(initialization)
  }
}