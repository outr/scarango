package com.outr.arango.audit

import cats.effect.IO
import com.outr.arango.{Document, Graph}
import com.outr.arango.collection.DocumentCollection
import fabric.Json
import fabric.rw._

trait AuditSupport {
  this: Graph =>

  val auditLog: DocumentCollection[AuditRecord] = vertex(AuditRecord)

  object audit {
    def resource(name: String): Resource = Resource(name)
  }

  case class Resource(name: String) {
    def record[T <: Document[T]: RW](action: String,
                                               value: T,
                                               origin: Option[String] = None,
                                               metadata: Map[String, Json] = Map.empty): IO[AuditRecord] = {
      val record = AuditRecord(
        action = action,
        resource = name,
        origin = origin,
        userRef = Some(value._id.json),
        value = Some(value.json),
        metadata = metadata
      )
      auditLog.insert(record).map(_ => record)
    }
  }
}