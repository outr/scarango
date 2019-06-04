package com.outr.arango.api.model

import io.circe.Json

/**
  * PostAPIIndexSkiplist
  *
  * @param type must be equal to {@literal *}"skiplist"{@literal *}.
  * @param deduplicate if {@literal *}false{@literal *}, the deduplication of array values is turned off.
  * @param fields an array of attribute paths.
  * @param sparse if {@literal *}true{@literal *}, then create a sparse index.
  * @param unique if {@literal *}true{@literal *}, then create a unique index.
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class PostAPIIndexSkiplist(`type`: String,
                                deduplicate: Option[Boolean] = None,
                                fields: Option[List[String]] = None,
                                sparse: Option[Boolean] = None,
                                unique: Option[Boolean] = None)