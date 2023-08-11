package com.outr.arango.core

import com.arangodb
import com.arangodb.ContentType
import com.arangodb.entity.{LoadBalancingStrategy => LBS}
import com.arangodb.serde.jackson.JacksonSerde
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind._
import fabric._

import scala.jdk.CollectionConverters._

class ArangoDBServer(connection: arangodb.ArangoDB) {
  lazy val db: ArangoDB = new ArangoDB(this, connection.db())

  def db(name: String): ArangoDB = new ArangoDB(this, connection.db(name))
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
  private lazy val serde: JacksonSerde = {
    val serde = JacksonSerde.of(ContentType.JSON)
    serde.configure { mapper =>
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
    serde
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
    case JsonNodeType.BOOLEAN => Bool(node.asBoolean())
    case JsonNodeType.NUMBER if node.canConvertToExactIntegral => NumInt(node.asLong())
    case JsonNodeType.NUMBER => NumDec(BigDecimal(node.decimalValue()))
    case JsonNodeType.STRING => Str(node.asText())
    case JsonNodeType.POJO | JsonNodeType.BINARY | JsonNodeType.MISSING =>
      throw new UnsupportedOperationException(s"Unsupported node type: ${node.getNodeType} - $node")
  }

  def apply(connection: arangodb.ArangoDB): ArangoDBServer = new ArangoDBServer(connection)

  def apply(config: ArangoDBConfig): ArangoDBServer = {
    val loadBalancingStrategy = config.loadBalancingStrategy match {
      case LoadBalancingStrategy.None => LBS.NONE
      case LoadBalancingStrategy.RoundRobin => LBS.ROUND_ROBIN
      case LoadBalancingStrategy.OneRandom => LBS.ONE_RANDOM
    }
    val builder = new arangodb.ArangoDB.Builder()
      .serde(serde)
      .user(config.username)
      .password(config.password)
      .useSsl(config.ssl)
      .timeout(Option(config.timeout).map(_.toMillis.toInt).map(Integer.valueOf).getOrElse(0))
      .acquireHostList(config.acquireHostList)
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