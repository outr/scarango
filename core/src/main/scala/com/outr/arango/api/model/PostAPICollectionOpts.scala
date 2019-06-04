package com.outr.arango.api.model

import io.circe.Json

/**
  * PostAPICollectionOpts
  *
  * @param allowUserKeys if set to {@literal *}true{@literal *}, then it is allowed to supply own key values in the
  *        {@literal *}_key{@literal *} attribute of a document. If set to {@literal *}false{@literal *}, then the key generator
  *        will solely be responsible for generating keys and supplying own key values
  *        in the {@literal *}_key{@literal *} attribute of documents is considered an error.
  * @param increment increment value for {@literal *}autoincrement{@literal *} key generator. Not used for other key
  *        generator types.
  * @param offset Initial offset value for {@literal *}autoincrement{@literal *} key generator.
  *        Not used for other key generator types.
  * @param type specifies the type of the key generator. The currently available generators are
  *        {@literal *}traditional{@literal *}, {@literal *}autoincrement{@literal *}, {@literal *}uuid{@literal *} and {@literal *}padded{@literal *}.
  *        
  *        The {@literal *}traditional{@literal *} key generator generates numerical keys in ascending order.
  *        The {@literal *}autoincrement{@literal *} key generator generates numerical keys in ascending order, 
  *        the inital offset and the spacing can be configured
  *        The {@literal *}padded{@literal *} key generator generates keys of a fixed length (16 bytes) in
  *        ascending lexicographical sort order. This is ideal for usage with the _RocksDB_
  *        engine, which will slightly benefit keys that are inserted in lexicographically
  *        ascending order. The key generator can be used in a single-server or cluster.
  *        The {@literal *}uuid{@literal *} key generator generates universally unique 128 bit keys, which 
  *        are stored in hexadecimal human-readable format. This key generator can be used
  *        in a single-server or cluster to generate "seemingly random" keys. The keys 
  *        produced by this key generator are not lexicographically sorted.
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class PostAPICollectionOpts(allowUserKeys: Option[Boolean] = None,
                                 increment: Option[Long] = None,
                                 offset: Option[Long] = None,
                                 `type`: Option[String] = None)