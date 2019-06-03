package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminWalTransactions(client: HttpClient) {
  val get = new AdminWalTransactionsGet(client)
}