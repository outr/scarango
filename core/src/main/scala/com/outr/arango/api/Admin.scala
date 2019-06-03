package com.outr.arango.api

import io.youi.client.HttpClient
          
class Admin(client: HttpClient) {
  val database = new AdminDatabase(client)
  val execute = new AdminExecute(client)
  val server = new AdminServer(client)
  val statistics = new AdminStatistics(client)
  val cluster = new AdminCluster(client)
  val shutdown = new AdminShutdown(client)
  val statisticsDescription = new AdminStatisticsDescription(client)
  val status = new AdminStatus(client)
  val clusterStatistics = new AdminClusterStatistics(client)
  val echo = new AdminEcho(client)
  val routing = new AdminRouting(client)
  val time = new AdminTime(client)
  val wal = new AdminWal(client)
  val log = new AdminLog(client)
}