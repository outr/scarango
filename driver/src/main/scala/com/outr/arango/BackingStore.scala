package com.outr.arango

import io.circe.Json

case class BackingStore(data: Json, _identity: Id[BackingStore]) extends Document[BackingStore]

object BackingStore extends DocumentModel[BackingStore] {
  override val collectionName: String = "backingStore"
  override implicit val serialization: Serialization[BackingStore] = Serialization.auto[BackingStore]
}