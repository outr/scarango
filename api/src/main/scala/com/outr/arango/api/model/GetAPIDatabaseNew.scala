package com.outr.arango.api.model

case class GetAPIDatabaseNew(name: String,
                             options: Option[CreateDatabaseOptions] = None,
                             users: Option[GetAPIDatabaseNewUSERS] = None)