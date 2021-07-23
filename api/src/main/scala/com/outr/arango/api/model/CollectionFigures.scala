package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFigures(alive: Option[CollectionFiguresAlive] = None,
                             compactionStatus: Option[CompactionStatusAttributes] = None,
                             compactors: Option[CollectionFiguresCompactors] = None,
                             datafiles: Option[CollectionFiguresDatafiles] = None,
                             dead: Option[CollectionFiguresDead] = None,
                             documentReferences: Option[Long] = None,
                             indexes: Option[CollectionFiguresIndexes] = None,
                             journals: Option[CollectionFiguresJournals] = None,
                             lastTick: Option[Long] = None,
                             readcache: Option[CollectionFiguresReadcache] = None,
                             revisions: Option[CollectionFiguresRevisions] = None,
                             uncollectedLogfileEntries: Option[Long] = None,
                             waitingFor: Option[String] = None)

object CollectionFigures {
  implicit val rw: ReaderWriter[CollectionFigures] = ccRW
}