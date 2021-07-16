package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAdminLoglevel(agency: Option[String] = None,
                            agencycomm: Option[String] = None,
                            auditAuthentication: Option[String] = None,
                            auditAuthorization: Option[String] = None,
                            auditCollection: Option[String] = None,
                            auditDatabase: Option[String] = None,
                            auditDocument: Option[String] = None,
                            auditService: Option[String] = None,
                            auditView: Option[String] = None,
                            authentication: Option[String] = None,
                            authorization: Option[String] = None,
                            cache: Option[String] = None,
                            cluster: Option[String] = None,
                            collector: Option[String] = None,
                            communication: Option[String] = None,
                            compactor: Option[String] = None,
                            config: Option[String] = None,
                            datafiles: Option[String] = None,
                            development: Option[String] = None,
                            engines: Option[String] = None,
                            general: Option[String] = None,
                            graphs: Option[String] = None,
                            heartbeat: Option[String] = None,
                            ldap: Option[String] = None,
                            memory: Option[String] = None,
                            mmap: Option[String] = None,
                            performance: Option[String] = None,
                            pregel: Option[String] = None,
                            queries: Option[String] = None,
                            replication: Option[String] = None,
                            requests: Option[String] = None,
                            rocksdb: Option[String] = None,
                            ssl: Option[String] = None,
                            startup: Option[String] = None,
                            supervision: Option[String] = None,
                            syscall: Option[String] = None,
                            threads: Option[String] = None,
                            trx: Option[String] = None,
                            v8: Option[String] = None,
                            views: Option[String] = None)

object PutAdminLoglevel {
  implicit val rw: ReaderWriter[PutAdminLoglevel] = ccRW
}