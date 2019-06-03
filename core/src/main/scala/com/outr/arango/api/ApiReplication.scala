package com.outr.arango.api

import io.youi.client.HttpClient
          
class ApiReplication(client: HttpClient) {
  val applierState = new ApiReplicationApplierState(client)
  val inventory = new ApiReplicationInventory(client)
  val applierStop = new ApiReplicationApplierStop(client)
  val loggerTickRanges = new ApiReplicationLoggerTickRanges(client)
  val loggerFollow = new ApiReplicationLoggerFollow(client)
  val clusterInventory = new ApiReplicationClusterInventory(client)
  val dump = new ApiReplicationDump(client)
  val batch = new ApiReplicationBatch(client)
  val applierStart = new ApiReplicationApplierStart(client)
  val sync = new ApiReplicationSync(client)
  val loggerFirstTick = new ApiReplicationLoggerFirstTick(client)
  val makeSlave = new ApiReplicationMakeSlave(client)
  val loggerState = new ApiReplicationLoggerState(client)
  val serverId = new ApiReplicationServerId(client)
  val applierConfig = new ApiReplicationApplierConfig(client)
}