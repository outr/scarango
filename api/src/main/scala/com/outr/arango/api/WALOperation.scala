package com.outr.arango.api

import io.circe.Json

case class WALOperation(tick: Long,
                        `type`: OperationType,
                        db: String,
                        data: Json = Json.obj(),
                        cuid: Option[String],
                        tid: Option[String]) {
  lazy val collectionId: Option[String] = cuid.flatMap { s =>
    val index = s.indexOf('/')
    if (index != -1) {
      Some(s.substring(index + 1))
    } else {
      None
    }
  }
}

sealed abstract class OperationType(val value: Int)

object OperationType {
  case object CreatedDatabase extends OperationType(1100)
  case object DropDatabase extends OperationType(1101)
  case object CreateCollection extends OperationType(2000)
  case object DropCollection extends OperationType(2001)
  case object RenameCollection extends OperationType(2002)
  case object ChangeCollection extends OperationType(2003)
  case object TruncateCollection extends OperationType(2004)
  case object CreateIndex extends OperationType(2100)
  case object DropIndex extends OperationType(2101)
  case object CreateView extends OperationType(2110)
  case object DropView extends OperationType(2111)
  case object ChangeView extends OperationType(2112)
  case object StartTransaction extends OperationType(2200)
  case object AbortTransaction extends OperationType(2201)
  case object InsertReplaceDocument extends OperationType(2300)
  case object RemoveDocument extends OperationType(2302)

  lazy val all: List[OperationType] = List(
    CreatedDatabase,
    DropDatabase,
    CreateCollection,
    DropCollection,
    RenameCollection,
    ChangeCollection,
    TruncateCollection,
    CreateIndex,
    DropIndex,
    CreateView,
    DropView,
    ChangeView,
    StartTransaction,
    AbortTransaction,
    InsertReplaceDocument,
    RemoveDocument
  )
  lazy val map: Map[Int, OperationType] = all.map(ot => ot.value -> ot).toMap

  def apply(value: Int): OperationType = map(value)
}