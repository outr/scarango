package com.outr.arango

sealed abstract class OnDuplicate(val value: String)

object OnDuplicate {
  case object Error extends OnDuplicate("error")
  case object Update extends OnDuplicate("update")
  case object Replace extends OnDuplicate("replace")
  case object Ignore extends OnDuplicate("ignore")
}