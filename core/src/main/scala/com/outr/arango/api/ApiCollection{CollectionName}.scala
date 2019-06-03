package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiCollection{CollectionName}(client: HttpClient) {
  val count = new ApiCollection{CollectionName}Count(client)
  val figures = new ApiCollection{CollectionName}Figures(client)
  val truncate = new ApiCollection{CollectionName}Truncate(client)
  val load = new ApiCollection{CollectionName}Load(client)
  val recalculateCount = new ApiCollection{CollectionName}RecalculateCount(client)
  val responsibleShard = new ApiCollection{CollectionName}ResponsibleShard(client)
  val unload = new ApiCollection{CollectionName}Unload(client)
  val rename = new ApiCollection{CollectionName}Rename(client)
  val loadIndexesIntoMemory = new ApiCollection{CollectionName}LoadIndexesIntoMemory(client)
  val rotate = new ApiCollection{CollectionName}Rotate(client)
  val revision = new ApiCollection{CollectionName}Revision(client)
  val checksum = new ApiCollection{CollectionName}Checksum(client)
  val properties = new ApiCollection{CollectionName}Properties(client)
  val delete = new ApiCollection{CollectionName}Delete(client)
  val get = new ApiCollection{CollectionName}Get(client)
}