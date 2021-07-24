package com.outr.arango

import com.outr.arango.api.model.{ArangoLinkFieldProperties, ArangoLinkProperties, PostAPIViewFields, PostAPIViewIresearch, PostAPIViewLinkProps, PostAPIViewProps, PostAPIViewPropsConsolidation, PutAPIViewPropertiesIresearch}
import com.outr.arango.api.{APIViewArangoSearch, APIViewViewNamePropertiesArangoSearch}
import fabric.rw.Asable
import io.youi.client.HttpClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ArangoView(client: HttpClient, dbName: String, viewName: String, `type`: String) {
  def create(cleanupIntervalStep: Int = 10,
             commitInterval: FiniteDuration = 1.second,
             consolidationInterval: FiniteDuration = 60.seconds,
             consolidationPolicy: Option[PostAPIViewPropsConsolidation] = None)
            (implicit ec: ExecutionContext): Future[ViewInfo] = APIViewArangoSearch.post(
    client = client,
    body = PostAPIViewIresearch(
      name = viewName,
      properties = Some(PostAPIViewProps(
        cleanupIntervalStep = Some(cleanupIntervalStep),
        commitIntervalMsec = Some(commitInterval.toMillis),
        consolidationIntervalMsec = Some(consolidationInterval.toMillis),
        consolidationPolicy = consolidationPolicy,
        links = None
      )),
      `type` = Some(`type`)
    )
  ).map(_.as[ViewInfo])

  def update(includeAllFields: Boolean,
             links: Option[List[ViewLink]] = None,
             cleanupIntervalStep: Option[Int] = None,
             commitInterval: Option[FiniteDuration] = None,
             consolidationInterval: Option[FiniteDuration] = None,
             consolidationPolicy: Option[PostAPIViewPropsConsolidation] = None)
            (implicit ec: ExecutionContext): Future[ViewInfo] = {
    val map = links.map(_.map { l =>
      l.collectionName -> ArangoLinkProperties(
        analyzers = l.analyzers,
        fields = l.fields,
        includeAllFields = includeAllFields,
        storeValues = if (l.allowExists) "id" else "none",
        trackListPositions = l.trackListPositions
      )
    }.toMap)
    APIViewViewNamePropertiesArangoSearch.put(
      client = client,
      viewName = viewName,
      body = PostAPIViewProps(
        cleanupIntervalStep = cleanupIntervalStep.map(_.toLong),
        commitIntervalMsec = commitInterval.map(_.toMillis),
        consolidationIntervalMsec = consolidationInterval.map(_.toMillis),
        consolidationPolicy = consolidationPolicy,
        links = map
      )
    ).map(_.as[ViewInfo])
  }
}