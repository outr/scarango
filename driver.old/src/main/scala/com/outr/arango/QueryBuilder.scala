package com.outr.arango

import com.outr.arango.api.model.{PostAPICursor, PostAPICursorOpts}
import com.outr.arango.api.{APICursor, APICursorCursorIdentifier}
import io.youi.client.HttpClient
import fabric._
import fabric.rw._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.language.experimental.macros

case class QueryBuilder[R](client: HttpClient,
                           query: Query,
                           writer: Writer[R],
                           batchSize: Int = 100,
                           cache: Boolean = true,
                           count: Boolean = false,
                           memoryLimit: Option[Long] = None,
                           options: Option[PostAPICursorOpts] = None,
                           ttl: Option[Long] = None,
                           logQuery: Option[Value => Unit] = None,
                           logResponse: Option[Value => Unit] = None) {
  implicit val rWriter: Writer[R] = writer
  implicit val qrWriter: Writer[QueryResponse[R]] = ccW

  def as[D: Writer]: QueryBuilder[D] = copy[D](writer = implicitly[Writer[D]])

  def batchSize(batchSize: Int): QueryBuilder[R] = copy(batchSize = batchSize)
  def withCache: QueryBuilder[R] = copy(cache = true)
  def withoutCache: QueryBuilder[R] = copy(cache = false)
  def includeCount: QueryBuilder[R] = copy(count = true)
  def excludeCount: QueryBuilder[R] = copy(count = false)
  def withMemoryLimit(limit: Long): QueryBuilder[R] = copy(memoryLimit = Some(limit))
  def withoutMemoryLimit: QueryBuilder[R] = copy(memoryLimit = None)
  def cursorTimeout(timeInSeconds: Int = 30): QueryBuilder[R] = copy(ttl = Some(timeInSeconds))
  def failOnWarning(b: Boolean): QueryBuilder[R] = opt(_.copy(failOnWarning = Some(b)))
  def includeFullCount: QueryBuilder[R] = opt(_.copy(fullCount = Some(true)))
  def excludeFullCount: QueryBuilder[R] = opt(_.copy(fullCount = Some(false)))
  def maxWarningCount(n: Int): QueryBuilder[R] = opt(_.copy(maxWarningCount = Some(n)))
  def maxRuntime(max: FiniteDuration): QueryBuilder[R] = opt(_.copy(maxRuntime = Some(max.toMillis.toDouble / 1000.0)))
  def satelliteSyncWait(b: Boolean): QueryBuilder[R] = opt(_.copy(satelliteSyncWait = Some(b)))
  def stream(b: Boolean): QueryBuilder[R] = opt(_.copy(stream = Some(b)))
  def logQuery(f: Value => Unit): QueryBuilder[R] = copy(logQuery = Some(f))
  def logResponse(f: Value => Unit): QueryBuilder[R] = copy(logResponse = Some(f))

  private def opt(f: PostAPICursorOpts => PostAPICursorOpts): QueryBuilder[R] = {
    val opts = options.getOrElse(PostAPICursorOpts())
    copy(options = Some(f(opts)))
  }

  def cursor(implicit ec: ExecutionContext): Future[QueryResponse[R]] = {
    val bindVars = query.bindVars
    val apiCursor = PostAPICursor(
      query = query.value,
      bindVars = bindVars,
      batchSize = Some(batchSize.toLong),
      cache = Some(cache),
      count = Some(count),
      memoryLimit = memoryLimit,
      options = options,
      ttl = ttl
    )
    logQuery.foreach(f => f(apiCursor.toValue))
    APICursor
      .post(
        client = client,
        body = apiCursor
      )
      .map { response =>
        logResponse.foreach(f => f(response))
        response.as[QueryResponse[R]]
      }
      .map { r =>
        if (options.flatMap(_.fullCount).getOrElse(false)) {
          r.copy(count = r.extra.stats.fullCount)
        } else {
          r
        }
      }
  }

  def update(implicit ec: ExecutionContext): Future[Unit] = cursor(ec).map(_ => ())

  def results(implicit ec: ExecutionContext): Future[List[R]] = cursor(ec).map(_.result)

  def get(id: String)(implicit ec: ExecutionContext): Future[QueryResponse[R]] = APICursorCursorIdentifier
    .put(client, id)
    .map(_.as[QueryResponse[R]])

  /**
    * Convenience method that calls `cursor` grabbing the first result returning None if there are no results.
    */
  def first(implicit ec: ExecutionContext): Future[Option[R]] = {
    batchSize(1).cursor.map(_.result.headOption)
  }

  def one(implicit ec: ExecutionContext): Future[R] = {
    batchSize(1).includeCount.cursor.map { response =>
      if (response.count != 1) {
        throw new RuntimeException(s"Expected exactly one result for $query, but received ${response.count}")
      } else {
        response.result.head
      }
    }
  }

  def paged(implicit ec: ExecutionContext): Future[Pagination[R]] = {
    includeCount.cursor.map(Pagination(this, _))
  }

  /**
    * Utilizes pagination to process through all pages of data
    *
    * @param f the function to handle processing of each page of data
    * @param ec the ExecutionContext
    * @return List[Return]
    */
  def process[Return](f: QueryResponse[R] => Future[Return])(implicit ec: ExecutionContext): Future[List[Return]] = {
    paged(ec).flatMap(_.process(f))
  }

  /**
    * Simplification of process to iteratively handle one result at a time through all pages of data
    *
    * @param f the function to handle processing of each data element
    * @param ec the ExecutionContext
    * @return Future[Unit]
    */
  def iterate(f: R => Future[Unit])(implicit ec: ExecutionContext): Future[Unit] = process { response =>
    def recurse(list: List[R]): Future[Unit] = if (list.isEmpty) {
      Future.successful(())
    } else {
      f(list.head).flatMap(_ => recurse(list.tail))
    }
    recurse(response.result)
  }.map(_ => ())
}