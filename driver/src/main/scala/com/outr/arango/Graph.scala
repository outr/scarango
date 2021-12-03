package com.outr.arango

import cats.effect.IO
import com.outr.arango.core.{ArangoDB, ArangoDBCollection, ArangoDBConfig, ArangoDBDocuments, ArangoDBServer, CollectionInfo}
import com.outr.arango.upgrade.{CreateDatabase, DatabaseUpgrade}
import fabric.obj
import fabric.rw._

import java.util.concurrent.atomic.AtomicBoolean

class Graph(private[arango] val db: ArangoDB) {
  private val _initialized = new AtomicBoolean(false)

  private var _collections: List[DocumentCollection[_]] = Nil
  private var _stores: List[DatabaseStore] = Nil
  protected def storeCollectionName: String = "backingStore"

  def collections: List[DocumentCollection[_]] = _collections
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

  def databaseName: String = db.name

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

  def vertex[D <: Document[D]](model: DocumentModel[D]): DocumentCollection[D] =
    collection(model, CollectionType.Vertex)
  def edge[D <: Document[D]](model: DocumentModel[D]): DocumentCollection[D] =
    collection(model, CollectionType.Edge)

  def collection[D <: Document[D]](model: DocumentModel[D], `type`: CollectionType): DocumentCollection[D] = synchronized {
    val c = new DocumentCollection[D](this, db.collection(model.collectionName), model, `type`)
    _collections = _collections ::: List(c)
    c
  }

  def keyStore(collectionName: String): DatabaseStore = synchronized {
    val s = DatabaseStore(db.collection(collectionName))
    _stores = _stores ::: List(s)
    s
  }
}

class DocumentCollection[D <: Document[D]](protected[arango] val graph: Graph,
                                           protected[arango] val collection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType) extends WritableCollection[D] {
  override def dbName: String = graph.databaseName
  override def name: String = collection.collection.name()

  override def query(query: Query): fs2.Stream[IO, D] = graph
    .db
    .query(query)
    .map(_.as[D](model.rw))
}

trait WritableCollection[D <: Document[D]] extends Collection[D] {
  protected def collection: ArangoDBCollection

  def create(): IO[CollectionInfo] = collection.create(model.collectionOptions)
  def exists(): IO[Boolean] = collection.exists()
  def drop(): IO[Unit] = collection.drop()
  def info(): IO[CollectionInfo] = collection.info()

  private implicit def rw: ReaderWriter[D] = model.rw

  private def string2Doc(json: String): D = fabric.parse.Json.parse(json).as[D]
  private def doc2String(doc: D): String = {
    val keys = doc match {
      case edge: Edge[_, _, _] => obj(
        "_key" -> edge._id.value,
        "_from" -> edge._from._id,
        "_to" -> edge._to._id
      )
      case _ => obj("_key" -> doc._id.value)
    }
    val value = doc.toValue.merge(keys)
    fabric.parse.Json.format(value)
  }

  lazy val document = new ArangoDBDocuments[D](collection.collection, string2Doc, doc2String)
}

trait Collection[D <: Document[D]] extends QueryPart.Support {
  def model: DocumentModel[D]
  def `type`: CollectionType
  def dbName: String
  def name: String

  def query(query: Query): fs2.Stream[IO, D]

  override def toQueryPart: QueryPart = QueryPart.Static(name)
}

case class AppliedUpgrades(labels: Set[String])

object AppliedUpgrades {
  implicit val rw: ReaderWriter[AppliedUpgrades] = ccRW

  val key: String = "appliedUpgrades"

  val empty: AppliedUpgrades = AppliedUpgrades(Set.empty)
}