package com.outr.arango.rest

case class BulkInserted(error: Boolean,
                        created: Int,
                        errors: Int,
                        empty: Int,
                        updated: Int,
                        ignored: Int,
                        details: Option[List[String]])
