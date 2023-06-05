package com.outr.arango.audit

import com.outr.arango.{Document, DocumentModel, Field, Id, Index}
import fabric.Json
import fabric.rw._

case class AuditRecord(action: String,
                       resource: String = "base",
                       origin: Option[String] = None,
                       sessionRef: Option[Json] = None,
                       userRef: Option[Json] = None,
                       organizationRef: Option[Json] = None,
                       value: Option[Json] = None,
                       valueRef: Option[Json] = None,
                       metadata: Map[String, Json] = Map.empty,
                       created: Long = System.currentTimeMillis(),
                       _id: Id[AuditRecord] = AuditRecord.id()) extends Document[AuditRecord]

object AuditRecord extends DocumentModel[AuditRecord] {
  val action: Field[String] = field("action")
  val resource: Field[String] = field("resource")
  val origin: Field[Option[String]] = field("origin")
  val sessionRef: Field[Option[Json]] = field("sessionRef")
  val userRef: Field[Option[Json]] = field("userRef")
  val organizationRef: Field[Option[Json]] = field("organizationRef")
  val value: Field[Option[Json]] = field("value")
  val valueRef: Field[Option[Json]] = field("valueRef")
  val metadata: Field[Map[String, Json]] = field("metadata")
  val created: Field[Long] = field("created")

  override val collectionName: String = "auditLog"
  override implicit val rw: RW[AuditRecord] = RW.gen

  override def indexes: List[Index] = List(
    action.index.persistent(),
    resource.index.persistent(),
    origin.index.persistent(),
    sessionRef.index.persistent(),
    userRef.index.persistent(),
    organizationRef.index.persistent(),
    valueRef.index.persistent(),
    created.index.persistent()
  )
}