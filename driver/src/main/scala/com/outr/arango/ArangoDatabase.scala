package com.outr.arango

import com.outr.arango.Value.{BigDecimalValue, BooleanValue, DoubleValue, IntValue, LongValue, SeqBigDecimalValue, SeqBooleanValue, SeqDoubleValue, SeqIntValue, SeqLongValue, SeqStringValue, StringValue}
import com.outr.arango.api.model.{GetAPIDatabaseNew, PostAPICursor, PostAPICursorOpts, PostApiQueryProperties}
import com.outr.arango.api.{APICursor, APIDatabase, APIDatabaseDatabaseName, APIQuery}
import com.outr.arango.model.{ArangoCode, ArangoResponse}
import io.circe.{Decoder, Json}
import io.youi.client.HttpClient
import io.youi.net.Path
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoDatabase(client: HttpClient, name: String) {
  def create()(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = {
    APIDatabase.post(client, GetAPIDatabaseNew(
      name = name
    )).map(JsonUtil.fromJson[ArangoResponse[Boolean]](_))
    // TODO: Support setting user
  }

  def drop()(implicit ec: ExecutionContext): Future[ArangoResponse[Boolean]] = {
    APIDatabaseDatabaseName.delete(client, name).map(JsonUtil.fromJson[ArangoResponse[Boolean]](_))
  }

  def collection(name: String): ArangoCollection = {
    new ArangoCollection(client.path(Path.parse(s"/_db/${this.name}/")), this.name, name)
  }

  lazy val query: ArangoQuery = new ArangoQuery(client.path(Path.parse(s"/_db/${this.name}/")))
}

case class ValidationResult(error: Boolean,
                            errorMessage: Option[String],
                            errorNum: Option[Int],
                            code: Int,
                            parsed: Boolean = false,
                            collections: List[String] = Nil,
                            bindVars: List[String] = Nil,
                            ast: List[AST] = Nil) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum.get)
}

case class AST(`type`: String,
               name: Option[String],
               id: Option[Int],
               subNodes: Option[List[AST]])