package com.outr.arango.monitored

import com.outr.arango.{Document, Id, NamedRef}

case class GetReferences[D <: Document[D]](ids: NamedRef, dependencyId: Id[D])