package com.outr.arango.util

import cats.effect.IO
import com.arangodb.entity
import com.arangodb.entity.ErrorEntity
import com.arangodb.model
import com.arangodb.model.{DocumentCreateOptions, DocumentDeleteOptions}
import com.outr.arango.{AQLParseResult, ASTNode, ArangoError, CollectionInfo, CollectionSchema, CollectionStatus, CollectionType, CreateOptions, CreateResult, CreateResults, DeleteOptions, DeleteResult, DeleteResults, IndexInfo, KeyType, Level, OverwriteMode}

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

  implicit def collectionSchemaConversion(cs: model.CollectionSchema): CollectionSchema = Option(cs) match {
    case Some(_) => CollectionSchema(
      rule = Option(cs.getRule),
      level = cs.getLevel,
      message = Option(cs.getMessage)
    )
    case None => CollectionSchema()
  }

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

  implicit def value2AnyRef(v: fabric.Value): AnyRef = v match {
    case fabric.Obj(map) => map.map {
      case (key, value) => key -> value2AnyRef(value)
    }
    case fabric.Str(s) => s
    case fabric.Num(n) => n.underlying()
    case fabric.Bool(b) => java.lang.Boolean.valueOf(b)
    case fabric.Arr(a) => a.map(value2AnyRef).asJava
    case fabric.Null => None.orNull
  }

  implicit def aqlParseEntityConversion(e: entity.AqlParseEntity): AQLParseResult = AQLParseResult(
    collections = e.getCollections.asScala.toList,
    bindVars = e.getBindVars.asScala.toList,
    ast = parseASTNodes(e.getAst.asScala.toList)
  )

  private def parseASTNodes(nodes: List[entity.AqlParseEntity.AstNode]): List[ASTNode] = nodes.map { n =>
    ASTNode(
      `type` = n.getType,
      subNodes = parseASTNodes(n.getSubNodes.asScala.toList),
      name = n.getName,
      id = n.getId,
      value = n.getValue
    )
  }

  implicit def createOptionsConversion(o: CreateOptions): model.DocumentCreateOptions = {
    val dco = new DocumentCreateOptions
    dco.waitForSync(o.waitForSync)
    dco.returnNew(o.returnNew)
    dco.returnOld(o.returnOld)
    dco.overwrite(o.overwrite != OverwriteMode.None)
    o.overwrite match {
      case OverwriteMode.None => // Not set
      case OverwriteMode.Ignore => dco.overwriteMode(model.OverwriteMode.ignore)
      case OverwriteMode.Replace => dco.overwriteMode(model.OverwriteMode.replace)
      case OverwriteMode.Update | OverwriteMode.UpdateMerge => dco.overwriteMode(model.OverwriteMode.update)
      case OverwriteMode.Conflict => dco.overwriteMode(model.OverwriteMode.conflict)
    }
    dco.silent(o.silent)
    o.streamTransactionId.foreach(dco.streamTransactionId)
    if (o.overwrite == OverwriteMode.UpdateMerge) {
      dco.mergeObjects(true)
    }
    dco
  }

  implicit def deleteOptionsConversion(o: DeleteOptions): model.DocumentDeleteOptions = {
    val ddo = new DocumentDeleteOptions
    ddo.waitForSync(o.waitForSync)
    ddo.ifMatch(o.ifMatch.orNull)
    ddo.returnOld(o.returnOld)
    ddo.silent(o.silent)
    ddo.streamTransactionId(o.streamTransactionId.orNull)
    ddo
  }

  implicit def multiDocumentCreateConversion(e: entity.MultiDocumentEntity[entity.DocumentCreateEntity[String]]): CreateResults = CreateResults(
    results = e.getDocumentsAndErrors.asScala.toList.map {
      case ce: entity.DocumentCreateEntity[String @unchecked] => Right(ce)
      case err: ErrorEntity => Left(err)
    }
  )

  implicit def multiDocumentDeleteConversion(e: entity.MultiDocumentEntity[entity.DocumentDeleteEntity[String]]): DeleteResults = DeleteResults(
    results = e.getDocumentsAndErrors.asScala.toList.map {
      case de: entity.DocumentDeleteEntity[String @unchecked] => Right(de)
      case err: ErrorEntity => Left(err)
    }
  )

  implicit def createDocumentEntityConversion(e: entity.DocumentCreateEntity[String]): CreateResult = CreateResult(
    key = Option(e.getKey),
    id = Option(e.getId),
    rev = Option(e.getRev),
    newDocument = Option(e.getNew).map(fabric.parse.Json.parse),
    oldDocument = Option(e.getOld).map(fabric.parse.Json.parse)
  )

  implicit def deleteDocumentEntityConversion(e: entity.DocumentDeleteEntity[String]): DeleteResult = DeleteResult(
    key = Option(e.getKey),
    id = Option(e.getId),
    rev = Option(e.getRev),
    oldDocument = Option(e.getOld).map(fabric.parse.Json.parse)
  )

  implicit def errorEntityConversion(e: entity.ErrorEntity): ArangoError = ArangoError(
    code = e.getCode,
    num = e.getErrorNum,
    message = e.getErrorMessage,
    exception = e.getException
  )
}