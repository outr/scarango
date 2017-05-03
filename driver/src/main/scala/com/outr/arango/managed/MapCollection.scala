package com.outr.arango.managed

import com.outr.arango.rest.CreateInfo
import com.outr.arango.{DocumentOption, DoubleValue, IntValue, StringValue, Value}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto._

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class MapCollection(graph: Graph, name: String) extends VertexCollection[KeyValuePair](graph, name) {
  override implicit val encoder: Encoder[KeyValuePair] = deriveEncoder[KeyValuePair]
  override implicit val decoder: Decoder[KeyValuePair] = deriveDecoder[KeyValuePair]

  lazy val map: ArangoMap = new ArangoMap(this)

  override protected def updateDocument(document: KeyValuePair, info: CreateInfo): KeyValuePair = {
    document.copy(_key = Option(info._key), _id = Option(info._id), _rev = Option(info._rev))
  }
}

class ArangoMap(collection: MapCollection,
                timeout: FiniteDuration = 10.seconds,
                maxResults: Int = 100) extends mutable.Map[String, Value] {
  override def +=(kv: (String, Value)): ArangoMap.this.type = {
    Await.result(collection.managed.upsert(KeyValuePair(kv._2, Some(kv._1))), timeout)
    this
  }

  override def -=(key: String): ArangoMap.this.type = {
    val success = Await.result(collection.managed.delete(KeyValuePair(Value(""), Some(key))), timeout)
    assert(success, s"Deletion of $key failed.")
    this
  }

  override def get(key: String): Option[Value] = {
    Await.result(future.get(key), timeout)
  }

  object future {
    def get(key: String): Future[Option[Value]] = collection.get(key).map(_.map(_.value))
    def apply(key: String): Future[Value] = get(key).map(_.get)

    def string(key: String): Future[String] = apply(key).map {
      case StringValue(s) => s
      case v => throw new RuntimeException(s"Expected String value for $key but got $v.")
    }
    def stringOption(key: String): Future[Option[String]] = get(key).map(_.map {
      case StringValue(s) => s
      case v => throw new RuntimeException(s"Expected String value for $key but got $v.")
    })
    def int(key: String): Future[Int] = apply(key).map {
      case IntValue(i) => i
      case v => throw new RuntimeException(s"Expected Int value for $key but got $v.")
    }
    def intOption(key: String): Future[Option[Int]] = get(key).map(_.map {
      case IntValue(i) => i
      case v => throw new RuntimeException(s"Expected Int value for $key but got $v.")
    })
    def double(key: String): Future[Double] = apply(key).map {
      case DoubleValue(d) => d
      case v => throw new RuntimeException(s"Expected Double value for $key but got $v.")
    }
    def doubleOption(key: String): Future[Option[Double]] = get(key).map(_.map {
      case DoubleValue(d) => d
      case v => throw new RuntimeException(s"Expected Double value for $key but got $v.")
    })
  }

  def string(key: String): String = apply(key) match {
    case StringValue(s) => s
    case v => throw new RuntimeException(s"Expected String value for $key but got $v.")
  }

  def stringOption(key: String): Option[String] = get(key).map {
    case StringValue(s) => s
    case v => throw new RuntimeException(s"Expected String value for $key but got $v.")
  }

  def int(key: String): Int = apply(key) match {
    case IntValue(i) => i
    case v => throw new RuntimeException(s"Expected Int value for $key but got $v.")
  }

  def intOption(key: String): Option[Int] = get(key).map {
    case IntValue(i) => i
    case v => throw new RuntimeException(s"Expected Int value for $key but got $v.")
  }

  def double(key: String): Double = apply(key) match {
    case DoubleValue(d) => d
    case v => throw new RuntimeException(s"Expected Double value for $key but got $v.")
  }

  def doubleOption(key: String): Option[Double] = get(key).map {
    case DoubleValue(d) => d
    case v => throw new RuntimeException(s"Expected Double value for $key but got $v.")
  }

  override def iterator: Iterator[(String, Value)] = {
    collection.iterator(collection.allQuery).map(kv => kv._key.get -> kv.value)
  }
}

case class KeyValuePair(value: Value,
                        _key: Option[String],
                        _id: Option[String] = None,
                        _rev: Option[String] = None) extends DocumentOption