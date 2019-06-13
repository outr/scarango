package com.outr.arango

import com.outr.arango.api.{APIIndex, APIIndexIndexHandle, APIIndexfulltext, APIIndexgeo, APIIndexhash, APIIndexpersistent, APIIndexskiplist}
import com.outr.arango.api.model.{PostAPIIndexFulltext, PostAPIIndexGeo, PostAPIIndexHash, PostAPIIndexPersistent, PostAPIIndexSkiplist}
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoIndex(client: HttpClient, dbName: String, collectionName: String) {
  def create(`type`: IndexType,
             fields: List[String],
             sparse: Boolean = false,
             unique: Boolean = false,
             deduplicate: Boolean = true,
             geoJson: Boolean = true,
             minLength: Long = 3L)
            (implicit ec: ExecutionContext): Future[IndexInfo] = {
    val future = `type` match {
      case IndexType.Hash => APIIndexhash.post(client, collectionName, PostAPIIndexHash("hash", Some(deduplicate), Some(fields), Some(sparse), Some(unique)))
      case IndexType.SkipList => APIIndexskiplist.post(client, collectionName, PostAPIIndexSkiplist("skiplist", Some(deduplicate), Some(fields), Some(sparse), Some(unique)))
      case IndexType.Persistent => APIIndexpersistent.post(client, collectionName, PostAPIIndexPersistent("persistent", Some(fields), Some(sparse), Some(unique)))
      case IndexType.Geo => APIIndexgeo.post(client, collectionName, PostAPIIndexGeo("geo", Some(fields), Some(geoJson.toString)))
      case IndexType.FullText => APIIndexfulltext.post(client, collectionName, PostAPIIndexFulltext("fulltext", Some(fields), Some(minLength)))
    }
    future.map(JsonUtil.fromJson[IndexInfo](_))
  }

  def list()(implicit ec: ExecutionContext): Future[IndexList] = {
    APIIndex.get(client, collectionName).map(JsonUtil.fromJson[IndexList](_))
  }

  def delete(handle: String)(implicit ec: ExecutionContext): Future[IndexDelete] = {
    APIIndexIndexHandle.delete(client, collectionName, handle).map(JsonUtil.fromJson[IndexDelete](_))
  }
}