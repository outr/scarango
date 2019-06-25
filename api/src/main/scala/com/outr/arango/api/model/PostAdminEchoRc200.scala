package com.outr.arango.api.model

import io.circe.Json


case class PostAdminEchoRc200(authorized: Boolean,
                              client: Option[AdminEchoClientStruct] = None,
                              cookies: Option[Json] = None,
                              database: Option[String] = None,
                              headers: Option[Json] = None,
                              internals: Option[Json] = None,
                              parameters: Option[Json] = None,
                              path: Option[String] = None,
                              prefix: Option[Json] = None,
                              protocol: Option[String] = None,
                              rawRequestBody: Option[List[String]] = None,
                              rawSuffix: Option[List[String]] = None,
                              requestBody: Option[String] = None,
                              requestType: Option[String] = None,
                              server: Option[AdminEchoServerStruct] = None,
                              suffix: Option[List[String]] = None,
                              url: Option[String] = None,
                              user: Option[String] = None)