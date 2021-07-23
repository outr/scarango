package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAdminEchoRc200(authorized: Boolean,
                              client: Option[AdminEchoClientStruct] = None,
                              cookies: Option[Value] = None,
                              database: Option[String] = None,
                              headers: Option[Value] = None,
                              internals: Option[Value] = None,
                              parameters: Option[Value] = None,
                              path: Option[String] = None,
                              prefix: Option[Value] = None,
                              protocol: Option[String] = None,
                              rawRequestBody: Option[List[String]] = None,
                              rawSuffix: Option[List[String]] = None,
                              requestBody: Option[String] = None,
                              requestType: Option[String] = None,
                              server: Option[AdminEchoServerStruct] = None,
                              suffix: Option[List[String]] = None,
                              url: Option[String] = None,
                              user: Option[String] = None)

object PostAdminEchoRc200 {
  implicit val rw: ReaderWriter[PostAdminEchoRc200] = ccRW
}