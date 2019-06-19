package com.outr.arango

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
