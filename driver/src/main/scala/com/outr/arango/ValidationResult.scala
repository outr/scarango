package com.outr.arango

import com.outr.arango.model.ArangoCode

case class ValidationResult(error: Boolean,
                            errorMessage: Option[String],
                            errorNum: Option[Int],
                            code: Int,
                            parsed: Boolean = false,
                            collections: List[String] = Nil,
                            bindVars: List[String] = Nil,
                            ast: List[AST] = Nil) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum.get)
}
