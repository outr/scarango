package com.outr.arango.core

import com.arangodb.DbName
import com.arangodb.async.ArangoDBAsync
import com.arangodb.entity.{LoadBalancingStrategy => LBS}
import com.arangodb.mapping.ArangoJack
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind._
import fabric._

import scala.jdk.CollectionConverters._

class ArangoDBServer(connection: ArangoDBAsync) {
  lazy val db: ArangoDB = new ArangoDB(this, connection.db())

  def db(name: String): ArangoDB = new ArangoDB(this, connection.db(DbName.of(name)))
}

object ArangoDBServer {
  private object serializer extends JsonSerializer[Json] {
    override def serialize(value: Json, gen: JsonGenerator, serializers: SerializerProvider): Unit =
      fabric2Jackson(value, gen)
  }
  private object deserializer extends JsonDeserializer[Json] {
    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Json =
      jackson2Fabric(p.getCodec.readTree[JsonNode](p))
  }
  private lazy val arangoJack: ArangoJack = {
    val j = new ArangoJack
    j.configure { mapper =>
      val module = new SimpleModule("FabricModule")
      val types = List(
        classOf[Obj], classOf[Null], classOf[Arr], classOf[Str], classOf[Num], classOf[NumInt], classOf[NumDec],
        classOf[Bool]
      )
      module.addSerializer(classOf[Json], serializer)
      module.addDeserializer(classOf[Json], deserializer)
      types.foreach { c =>
        module.addDeserializer(c.asInstanceOf[Class[Json]], deserializer)
      }
      mapper.registerModule(module)
    }
    j
  }

  private def fabric2Jackson(json: Json, g: JsonGenerator): Unit = json match {
    case Null => g.writeNull()
    case Obj(map) =>
      g.writeStartObject()
      map.foreach {
        case (key, value) =>
          g.writeFieldName(key)
          fabric2Jackson(value, g)
      }
      g.writeEndObject()
    case Arr(vector) =>
      g.writeStartArray()
      vector.foreach(fabric2Jackson(_, g))
      g.writeEndArray()
    case Bool(b) => g.writeBoolean(b)
    case NumDec(d) => g.writeNumber(d.underlying())
    case NumInt(l) => g.writeNumber(l)
    case Str(s) => g.writeString(s)
  }

  private def jackson2Fabric(node: JsonNode): Json = node.getNodeType match {
    case JsonNodeType.NULL => Null
    case JsonNodeType.OBJECT => node
      .fields()
      .asScala
      .map { entry =>
        entry.getKey -> jackson2Fabric(entry.getValue)
      }
      .toMap
    case JsonNodeType.ARRAY => Arr((0 until node.size()).map { index =>
      jackson2Fabric(node.get(index))
    }.toVector)
    case JsonNodeType.POJO => ???
    case JsonNodeType.BINARY => ???
    case JsonNodeType.BOOLEAN => Bool(node.asBoolean())
    case JsonNodeType.MISSING => ???
    case JsonNodeType.NUMBER if node.canConvertToLong => NumInt(node.asLong())
    case JsonNodeType.NUMBER => NumDec(BigDecimal(node.asDouble())) // TODO: Is there a better way to do this?
    case JsonNodeType.STRING => Str(node.asText())
  }

  def apply(connection: ArangoDBAsync): ArangoDBServer = new ArangoDBServer(connection)

  def apply(config: ArangoDBConfig): ArangoDBServer = {
    val loadBalancingStrategy = config.loadBalancingStrategy match {
      case LoadBalancingStrategy.None => LBS.NONE
      case LoadBalancingStrategy.RoundRobin => LBS.ROUND_ROBIN
      case LoadBalancingStrategy.OneRandom => LBS.ONE_RANDOM
    }
    val builder = new ArangoDBAsync.Builder()
      .serializer(arangoJack)
      .user(config.username)
      .password(config.password)
      .useSsl(config.ssl)
      .timeout(Option(config.timeout).map(_.toMillis.toInt).map(Integer.valueOf).getOrElse(0))
      .acquireHostList(config.acquireHostList)
      .chunksize(config.chunkSize match {
        case -1 => null
        case n => n
      })
      .connectionTtl(Option(config.connectionTtl).map(_.toMillis).map(java.lang.Long.valueOf).orNull)
      .keepAliveInterval(Option(config.keepAliveInterval).map(_.toMillis.toInt).map(Integer.valueOf).orNull)
      .loadBalancingStrategy(loadBalancingStrategy)
      .maxConnections(config.maxConnections)
    config.hosts.foreach { host =>
      builder.host(host.host, host.port)
    }
    apply(builder.build())
  }

  def apply(): ArangoDBServer = apply(ArangoDBConfig())
}