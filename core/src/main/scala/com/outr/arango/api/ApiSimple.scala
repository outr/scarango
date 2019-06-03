package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiSimple(client: HttpClient) {
  val removeByExample = new ApiSimpleRemoveByExample(client)
  val any = new ApiSimpleAny(client)
  val allKeys = new ApiSimpleAllKeys(client)
  val all = new ApiSimpleAll(client)
  val firstExample = new ApiSimpleFirstExample(client)
  val lookupByKeys = new ApiSimpleLookupByKeys(client)
  val within = new ApiSimpleWithin(client)
  val replaceByExample = new ApiSimpleReplaceByExample(client)
  val removeByKeys = new ApiSimpleRemoveByKeys(client)
  val withinRectangle = new ApiSimpleWithinRectangle(client)
  val near = new ApiSimpleNear(client)
  val fulltext = new ApiSimpleFulltext(client)
  val range = new ApiSimpleRange(client)
  val updateByExample = new ApiSimpleUpdateByExample(client)
  val byExample = new ApiSimpleByExample(client)
}