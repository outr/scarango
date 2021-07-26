package com.outr.arango.util

import cats.effect.IO
import com.arangodb.entity
import com.arangodb.entity.BaseDocument
import com.arangodb.model
import com.outr.arango.{ArangoDocument, CollectionInfo, CollectionSchema, CollectionStatus, CollectionType, Id, Index, IndexInfo, KeyType, Level}

import java.util.concurrent.CompletableFuture
import scala.jdk.FutureConverters._
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

object Helpers {
  implicit class CompletableFutureExtras[T](cf: CompletableFuture[T]) {
    def toIO: IO[T] = IO.fromFuture(IO(cf.asScala))
  }

  implicit def collectionEntityConversion(ce: entity.CollectionEntity): CollectionInfo = CollectionInfo(
    id = ce.getId,
    name = ce.getName,
    waitForSync = ce.getWaitForSync,
    isVolatile = ce.getIsVolatile,
    isSystem = ce.getIsSystem,
    status = ce.getStatus,
    `type` = ce.getType,
    schema = ce.getSchema
  )

  implicit def statusConversion(status: entity.CollectionStatus): CollectionStatus = status match {
    case entity.CollectionStatus.NEW_BORN_COLLECTION => CollectionStatus.New
    case entity.CollectionStatus.UNLOADED => CollectionStatus.Unloaded
    case entity.CollectionStatus.LOADED => CollectionStatus.Loaded
    case entity.CollectionStatus.IN_THE_PROCESS_OF_BEING_UNLOADED => CollectionStatus.Loading
    case entity.CollectionStatus.DELETED => CollectionStatus.Deleted
  }

  implicit def keyTypeConversionFromJava(kt: entity.KeyType): KeyType = kt match {
    case entity.KeyType.traditional => KeyType.Traditional
    case entity.KeyType.autoincrement => KeyType.AutoIncrement
    case entity.KeyType.uuid => KeyType.UUID
    case entity.KeyType.padded => KeyType.Padded
  }
  
  implicit def keyTypeConversionToJava(kt: KeyType): entity.KeyType = kt match {
    case KeyType.Traditional => entity.KeyType.traditional
    case KeyType.AutoIncrement => entity.KeyType.autoincrement
    case KeyType.UUID => entity.KeyType.uuid
    case KeyType.Padded => entity.KeyType.padded
  }

  implicit def collectionTypeConversion(ct: entity.CollectionType): CollectionType = ct match {
    case entity.CollectionType.DOCUMENT => CollectionType.Document
    case entity.CollectionType.EDGES => CollectionType.Edge
  }

  implicit def collectionSchemaConversion(cs: model.CollectionSchema): CollectionSchema = CollectionSchema(
    rule = Option(cs.getRule),
    level = cs.getLevel,
    message = Option(cs.getMessage)
  )

  implicit def levelConversion(l: model.CollectionSchema.Level): Option[Level] = Option(l).map(_.name()).flatMap {
    case "none" => Some(Level.None)
    case "new" => Some(Level.New)
    case "moderate" => Some(Level.Moderate)
    case "strict" => Some(Level.Strict)
    case _ => None
  }

  implicit def option2Integer(i: Option[Int]): Integer = i.map(Integer.valueOf).orNull

  implicit def indexEntityConversion(e: entity.IndexEntity): IndexInfo = IndexInfo(
    `type` = e.getType.name(),
    fields = Option(e.getFields).map(_.asScala.toList),
    unique = Option(e.getUnique),
    sparse = Option(e.getSparse),
    id = e.getId,
    isNewlyCreated = Option(e.getIsNewlyCreated),
    selectivityEstimate = Option(e.getSelectivityEstimate)
  )

  implicit def documentConversion(d: ArangoDocument): BaseDocument = {
    val o = new BaseDocument(d._key)



    o
  }
}