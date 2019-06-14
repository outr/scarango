package com.outr.arango

case class Field[F](name: String) {
  object index {
    def hash(sparse: Boolean = false,
             unique: Boolean = false,
             deduplicate: Boolean = true): Index = {
      Index(IndexType.Hash, List(name), sparse, unique, deduplicate)
    }
    def skipList(sparse: Boolean = false,
                 unique: Boolean = false,
                 deduplicate: Boolean = true): Index = {
      Index(IndexType.SkipList, List(name), sparse, unique, deduplicate)
    }
    def persistent(sparse: Boolean = false,
                 unique: Boolean = false): Index = {
      Index(IndexType.Persistent, List(name), sparse, unique)
    }
    def geo(geoJson: Boolean = true): Index = {
      Index(IndexType.Geo, List(name), geoJson = geoJson)
    }
    def fullText(minLength: Long = 3L): Index = {
      Index(IndexType.FullText, List(name), minLength = minLength)
    }
  }

  lazy val opt: Field[Option[F]] = Field[Option[F]](name)
}

case class Index(`type`: IndexType,
                 fields: List[String],
                 sparse: Boolean = false,
                 unique: Boolean = false,
                 deduplicate: Boolean = true,
                 geoJson: Boolean = true,
                 minLength: Long = 3L) {
  def typeAndFields(info: IndexInfo): Boolean = info.`type` == `type`.toString.toLowerCase &&
    info.fields.contains(fields)
  def matches(info: IndexInfo): Boolean = typeAndFields(info) &&
    (info.unique.isEmpty || info.unique.contains(unique)) &&
    (info.sparse.isEmpty || info.sparse.contains(sparse))
}