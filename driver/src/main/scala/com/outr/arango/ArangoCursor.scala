package com.outr.arango

import com.outr.arango.Value._
import com.outr.arango.api.APICursor
import com.outr.arango.api.model.{PostAPICursor, PostAPICursorOpts}
import io.circe.{Decoder, Json}
import io.circe.generic.auto._
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoCursor(client: HttpClient, dbName: String, collectionName: String) {

}