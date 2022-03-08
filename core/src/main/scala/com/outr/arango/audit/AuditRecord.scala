package com.outr.arango.audit

import com.outr.arango.{Document, DocumentModel, Field, Id, Index}
import fabric.Value
import fabric.rw._

case class AuditRecord(action: String,
                       resource: String = "base",
                       origin: Option[String] = None,
                       sessionRef: Option[Value] = None,
                       userRef: Option[Value] = None,
                       value: Option[Value] = None,
                       metadata: Map[String, Value] = Map.empty,
                       created: Long = System.currentTimeMillis(),
                       _id: Id[AuditRecord] = AuditRecord.id()) extends Document[AuditRecord]

object AuditRecord extends DocumentModel[AuditRecord] {
  val action: Field[String] = field("action")
  val resource: Field[String] = field("resource")
  val origin: Field[Option[String]] = field("origin")
  val sessionRef: Field[Option[Value]] = field("sessionRef")
  val userRef: Field[Option[Value]] = field("userRef")
  val value: Field[Option[Value]] = field("value")
  val metadata: Field[Map[String, Value]] = field("metadata")
  val created: Field[Long] = field("created")

  override val collectionName: String = "auditLog"
  override implicit val rw: ReaderWriter[AuditRecord] = ccRW

  override def indexes: List[Index] = List(
    action.index.persistent(),
    resource.index.persistent(),
    origin.index.persistent(),
    sessionRef.index.persistent(),
    userRef.index.persistent(),
    created.index.persistent()
  )
}