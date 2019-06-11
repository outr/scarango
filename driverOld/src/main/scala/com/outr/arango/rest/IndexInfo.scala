package com.outr.arango.rest

case class IndexInfo(`type`: String,
                     fields: Option[List[String]] = None,
                     unique: Option[Boolean] = None,
                     sparse: Option[Boolean] = None,
                     id: Option[String] = None,
                     isNewlyCreated: Option[Boolean] = None,
                     selectivityEstimate: Option[Int] = None,
                     error: Boolean = false,
                     code: Int = 0)