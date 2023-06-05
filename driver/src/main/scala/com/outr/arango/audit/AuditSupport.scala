package com.outr.arango.audit

import cats.effect.IO
import com.outr.arango.collection.{DocumentCollection, QueryBuilder}
import com.outr.arango.query.SortDirection
import com.outr.arango.query.dsl._
import com.outr.arango.{Document, Field, Graph}
import fabric.Json
import fabric.rw._

trait AuditSupport {
  this: Graph =>

  val auditLog: DocumentCollection[AuditRecord, AuditRecord.type] = vertex(AuditRecord)

  object audit {
    def resource(name: String): Resource = Resource(name)
  }

  case class Resource(name: String) {
    def query(action: Option[String] = None,
              origin: Option[String] = None,
              sessionRef: Option[Json] = None,
              userRef: Option[Json] = None,
              organizationRef: Option[Json] = None,
              valueRef: Option[Json] = None,
              createdAfter: Option[Long] = None,
              createdBefore: Option[Long] = None,
              sortField: Field[_] = AuditRecord.created,
              sortDirection: SortDirection = SortDirection.DESC): QueryBuilder[AuditRecord] = {
      auditLog.query.byFilter({ r =>
        val filters = List(
          action.map(s => r.action === s),
          origin.map(s => r.origin === s),
          sessionRef.map(j => r.sessionRef === j),
          userRef.map(j => r.userRef === j),
          organizationRef.map(j => r.organizationRef === j),
          valueRef.map(j => r.valueRef === j),
          createdAfter.map(l => r.created > l),
          createdBefore.map(l => r.created < l)
        ).flatten
        filters.foldLeft(r.resource === name)((combined, filter) => combined && filter)
      }, (sortField, sortDirection))
    }

    def record[T <: Document[T] : RW](action: String,
                                      value: T,
                                      origin: Option[String] = None,
                                      sessionRef: Option[Json] = None,
                                      userRef: Option[Json] = None,
                                      organizationRef: Option[Json] = None,
                                      metadata: Map[String, Json] = Map.empty): IO[AuditRecord] = {
      val record = AuditRecord(
        action = action,
        resource = name,
        origin = origin,
        sessionRef = sessionRef,
        userRef = userRef,
        organizationRef = organizationRef,
        value = Some(value.json),
        valueRef = Some(value._id.json),
        metadata = metadata
      )
      auditLog.insert(record).map(_ => record)
    }
  }
}