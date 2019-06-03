package com.outr.arango.api

import io.youi.client.HttpClient
          
class AdminServerRole(client: HttpClient) {
  val get = new AdminServerRoleGet(client)
}