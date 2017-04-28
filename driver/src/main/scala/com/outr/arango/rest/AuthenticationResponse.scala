package com.outr.arango.rest

case class AuthenticationResponse(jwt: String, must_change_password: Boolean)