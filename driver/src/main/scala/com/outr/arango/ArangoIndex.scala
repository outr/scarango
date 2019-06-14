package com.outr.arango

import com.outr.arango.api.{APIIndex, APIIndexIndexHandle, APIIndexfulltext, APIIndexgeo, APIIndexhash, APIIndexpersistent, APIIndexskiplist}
import com.outr.arango.api.model.{PostAPIIndexFulltext, PostAPIIndexGeo, PostAPIIndexHash, PostAPIIndexPersistent, PostAPIIndexSkiplist}
import io.youi.client.HttpClient
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}

class ArangoIndex(client: HttpClient, dbName: String, collectionName: String) {
  def create(index: Index)(implicit ec: ExecutionContext): Future[IndexInfo] = {
    val future = index.`type` match {
      case IndexType.Hash => APIIndexhash.post(client, collectionName, PostAPIIndexHash("hash", Some(index.deduplicate), Some(index.fields), Some(index.sparse), Some(index.unique)))
      case IndexType.SkipList => APIIndexskiplist.post(client, collectionName, PostAPIIndexSkiplist("skiplist", Some(index.deduplicate), Some(index.fields), Some(index.sparse), Some(index.unique)))
      case IndexType.Persistent => APIIndexpersistent.post(client, collectionName, PostAPIIndexPersistent("persistent", Some(index.fields), Some(index.sparse), Some(index.unique)))
      case IndexType.Geo => APIIndexgeo.post(client, collectionName, PostAPIIndexGeo("geo", Some(index.fields), Some(index.geoJson.toString)))
      case IndexType.FullText => APIIndexfulltext.post(client, collectionName, PostAPIIndexFulltext("fulltext", Some(index.fields), Some(index.minLength)))
    }
    future.map(JsonUtil.fromJson[IndexInfo](_))
  }

  def list()(implicit ec: ExecutionContext): Future[IndexList] = {
    APIIndex.get(client, collectionName).map(JsonUtil.fromJson[IndexList](_))
  }

  def delete(id: Id[Index])(implicit ec: ExecutionContext): Future[IndexDelete] = {
    APIIndexIndexHandle.delete(client, collectionName, id.value).map(JsonUtil.fromJson[IndexDelete](_))
  }
}