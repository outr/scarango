package com.outr.arango.monitored

import com.outr.arango.NamedRef

case class QueryInfo(baseRef: NamedRef, ids: NamedRef, updatedRef: NamedRef)