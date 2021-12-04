package com.outr.arango.query

import com.outr.arango.Field

case class Sort(field: Field[_], direction: SortDirection)