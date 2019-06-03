package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminWal(client: HttpClient) {
  val flush = new AdminWalFlush(client)
  val properties = new AdminWalProperties(client)
  val transactions = new AdminWalTransactions(client)
}