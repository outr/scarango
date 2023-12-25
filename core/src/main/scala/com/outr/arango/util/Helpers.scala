package com.outr.arango.util

import cats.effect.IO
import com.arangodb.entity.ErrorEntity
import com.arangodb.model.{DocumentCreateOptions, DocumentDeleteOptions, DocumentUpdateOptions, AqlQueryOptions}
import com.arangodb.{ArangoDBException, entity, model}
import com.outr.arango._
import com.outr.arango.core._
import com.outr.arango.query.QueryOptions
import fabric._

import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

object Helpers {
  def io[Return](f: => Return): IO[Return] = IO
    .blocking(f)
    .recover {
      case t: ArangoDBException => throw ArangoException(t)
      case t => throw t
    }

  implicit def collectionEntityConversion(ce: entity.CollectionEntity): CollectionInfo = CollectionInfo(
    name = ce.getName,
    waitForSync = ce.getWaitForSync,
    isSystem = ce.getIsSystem,
    status = ce.getStatus,
    `type` = ce.getType,
    schema = ce.getSchema,
    computedValues = Option(ce.getComputedValues).map(_.asScala.toList).getOrElse(Nil)
  )

  implicit def computedValuesConversion(list: List[model.ComputedValue]): List[ComputedValue] = list.map { ce =>
    ComputedValue(
      name = ce.getName,
      expression = ce.getExpression,
      overwrite = ce.getOverwrite,
      computeOn = ce.getComputeOn.asScala.map {
        case model.ComputedValue.ComputeOn.insert => ComputeOn.Insert
        case model.ComputedValue.ComputeOn.update => ComputeOn.Update
        case model.ComputedValue.ComputeOn.replace => ComputeOn.Replace
      }.toSet,
      keepNull = ce.getKeepNull,
      failOnWarning = ce.getFailOnWarning
    )
  }

  implicit def statusConversion(status: entity.CollectionStatus): CollectionStatus = status match {
    case entity.CollectionStatus.LOADED => CollectionStatus.Loaded
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
    case entity.CollectionType.DOCUMENT => CollectionType.Vertex
    case entity.CollectionType.EDGES => CollectionType.Edge
  }

  implicit def collectionTypeConversionToJava(ct: CollectionType): entity.CollectionType = ct match {
    case CollectionType.Vertex => entity.CollectionType.DOCUMENT
    case CollectionType.Edge => entity.CollectionType.EDGES
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

  implicit def value2AnyRef(v: fabric.Json): AnyRef = v match {
    case fabric.Obj(map) => map.map {
      case (key, value) => key -> value2AnyRef(value)
    }.asJava
    case fabric.Str(s) => s
    case fabric.NumInt(n) => Long.box(n)
    case fabric.NumDec(n) => n.underlying()
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
    o.overwrite match {
      case OverwriteMode.None => // Not set
      case OverwriteMode.Ignore => dco.overwriteMode(model.OverwriteMode.ignore)
      case OverwriteMode.Replace => dco.overwriteMode(model.OverwriteMode.replace)
      case OverwriteMode.Update | OverwriteMode.UpdateMerge => dco.overwriteMode(model.OverwriteMode.update)
      case OverwriteMode.Conflict => dco.overwriteMode(model.OverwriteMode.conflict)
    }
    dco.silent(o.silent)
    o.streamTransaction.map(_.id).foreach(dco.streamTransactionId)
    if (o.overwrite == OverwriteMode.UpdateMerge) {
      dco.mergeObjects(true)
    }
    dco
  }

  implicit def updateOptionsConversion(o: UpdateOptions): model.DocumentUpdateOptions = {
    val duo = new DocumentUpdateOptions
    duo.keepNull(o.keepNull)
    duo.mergeObjects(o.mergeObjects)
    duo.waitForSync(o.waitForSync)
    duo.ignoreRevs(o.ignoreRevs)
    duo.ifMatch(o.ifMatch.orNull)
    duo.returnNew(o.returnNew)
    duo.returnOld(o.returnOld)
    duo.silent(o.silent)
    duo.streamTransactionId(o.streamTransaction.map(_.id).orNull)
    duo
  }

  implicit def deleteOptionsConversion(o: DeleteOptions): model.DocumentDeleteOptions = {
    val ddo = new DocumentDeleteOptions
    ddo.waitForSync(o.waitForSync)
    ddo.ifMatch(o.ifMatch.orNull)
    ddo.returnOld(o.returnOld)
    ddo.silent(o.silent)
    ddo.streamTransactionId(o.streamTransaction.map(_.id).orNull)
    ddo
  }

  implicit def multiDocumentCreateConversion[T](e: entity.MultiDocumentEntity[entity.DocumentCreateEntity[Json]],
                                                toT: Json => T,
                                                documents: List[T]): CreateResults[T] = CreateResults(
    results = e.getDocumentsAndErrors.asScala.toList.zip(documents).map {
      case (ce: entity.DocumentCreateEntity[Json @unchecked], document) => Right(createDocumentEntityConversion(ce, toT, document))
      case (err: ErrorEntity, _) => Left(err)
    }
  )

  implicit def multiDocumentDeleteConversion[T](e: entity.MultiDocumentEntity[entity.DocumentDeleteEntity[Json]], toT: Json => T): DeleteResults[T] = DeleteResults(
    results = e.getDocumentsAndErrors.asScala.toList.map {
      case de: entity.DocumentDeleteEntity[Json @unchecked] => Right(deleteDocumentEntityConversion(de, toT))
      case err: ErrorEntity => Left(err)
    }
  )

  implicit def createDocumentEntityConversion[T](e: entity.DocumentCreateEntity[Json], toT: Json => T, document: T): CreateResult[T] = CreateResult(
    key = Option(e.getKey),
    id = Option(e.getId),
    rev = Option(e.getRev),
    document = document,
    newDocument = Option(e.getNew).map(toT),
    oldDocument = Option(e.getOld).map(toT)
  )

  implicit def updateDocumentEntityConversion[T](e: entity.DocumentUpdateEntity[Json], toT: Json => T): UpdateResult[T] = UpdateResult(
    key = Option(e.getKey),
    id = Option(e.getId),
    rev = Option(e.getRev),
    oldRev = Option(e.getOldRev),
    newDocument = Option(e.getNew).map(toT),
    oldDocument = Option(e.getOld).map(toT)
  )

  implicit def deleteDocumentEntityConversion[T](e: entity.DocumentDeleteEntity[Json], toT: Json => T): DeleteResult[T] = DeleteResult(
    key = Option(e.getKey),
    id = Option(e.getId),
    rev = Option(e.getRev),
    oldDocument = Option(e.getOld).map(toT)
  )

  implicit def errorEntityConversion(e: entity.ErrorEntity): ArangoError = ArangoError(
    code = e.getCode,
    num = e.getErrorNum,
    message = e.getErrorMessage,
    exception = e.getException
  )

  implicit def queryOptions2Arango(e: QueryOptions): AqlQueryOptions = {
    val o = new AqlQueryOptions
    e.count.foreach(o.count(_))
    e.batchSize.foreach(o.batchSize(_))
    e.ttl.foreach(d => o.ttl(d.toSeconds.toInt))
    e.cache.foreach(o.cache(_))
    e.memoryLimit.foreach(o.memoryLimit(_))
    e.fullCount.foreach(o.fullCount(_))
    e.fillBlockCache.foreach(o.fillBlockCache(_))
    e.maxNumberOfPlans.foreach(o.maxPlans(_))
    e.maxWarningCount.foreach(o.maxWarningCount(_))
    e.failOnWarning.foreach(o.failOnWarning(_))
    e.allowRetry.foreach(o.allowRetry(_))
    e.stream.foreach(o.stream(_))
    e.profile.foreach(o.profile(_))
    e.satelliteSyncWait.foreach(d => o.satelliteSyncWait(d.toMillis / 1000.0))
    e.maxRuntime.foreach(d => o.maxRuntime(d.toMillis / 1000.0))
    e.maxTransactionSize.foreach(o.maxTransactionSize(_))
    e.intermediateCommitSize.foreach(o.intermediateCommitSize(_))
    e.intermediateCommitCount.foreach(o.intermediateCommitCount(_))
    e.skipInaccessibleCollections.foreach(o.skipInaccessibleCollections(_))
    o
  }
}