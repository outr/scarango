package com.outr.arango.util

import java.nio.file.{Files, Path, Paths}
import scala.annotation.tailrec
import scala.collection.immutable.ListMap

/**
  * WARNING: This is a work-in-progress and not ready for use yet.
  */
object Generator {
  def main(args: Array[String]): Unit = {
    val cf = apply(Paths.get("..", "example", "src", "main", "scala", "example", "Person.scala"))
    scribe.info(cf.updatedSource)
  }

  private val PackageRegex = """package (.+)""".r
  private val ParamRegex = """(.+):\s*(\S+)(?:\s+=\s+(\S+))?""".r

  private def detectCaseClasses(source: String): List[CaseClass] = {
    "case class (\\S+)[(]".r.findAllMatchIn(source).toList.map { m =>
      val b = new StringBuilder(m.group(0))
      var openParenthesis = 1
      var openQuotes = false

      @tailrec
      def recurse(position: Int): Unit = {
        val c = source.charAt(position)
        if (!openQuotes) {
          if (c == '(') {
            openParenthesis += 1
          } else if (c == ')') {
            openParenthesis -= 1
          }
        } else if (c == '"') {
          openQuotes = !openQuotes
        }
        b.append(c)
        if (c == ')' && openParenthesis == 0) {
          // Finished
        } else {
          recurse(position + 1)
        }
      }
      recurse(m.end)
      val Regex = """\s*(?:extends\s*(\S+))?(?:\s*with\s*(\S+))*""".r
      val tail = Regex.findFirstIn(source.substring(m.start + b.length())).get
      b.append(tail)

      val ccs = b.toString()
      val className = m.group(1)
      val paramsStart = ccs.indexOf('(')
      val paramsEnd = ccs.lastIndexOf(')')
      val params = ListMap.from(ccs.substring(paramsStart + 1, paramsEnd).splitOn(',').map {
        case ParamRegex(name, paramType, default) =>
          scribe.debug(s"$name, Default: $default")
          val n = name.trim
          n -> Param(
            name = n,
            `type` = paramType.trim,
            default = Option(default).map(_.trim)
          )
        case s => throw new RuntimeException(s"Unable to parse param: [$s]")
      })
      val extras = ccs.substring(paramsEnd + 1)
      val mixIns = extras
        .split("\\s+")
        .filter(_.nonEmpty)
        .grouped(2)
        .map(_.last)
        .toList
      CaseClass(
        className = className,
        params = params,
        mixIns = mixIns,
        start = m.start,
        end = m.start + ccs.length
      ).update()
    }
  }

  implicit class StringExtras(s: String) {
    def splitOn(split: Char): List[String] = {
      var l = List.empty[String]
      val b = new StringBuilder
      var quotes = false

      s.foreach { c =>
        if (c == '"') {
          quotes = !quotes
        }
        if (!quotes && c == split) {
          l = b.toString().trim :: l
          b.clear()
        } else {
          b.append(c)
        }
      }
      if (b.nonEmpty) {
        l = b.toString().trim :: l
      }
      l.reverse
    }
  }

  def apply(codeFile: Path): CodeFile = {
    val source = Files.readString(codeFile)
    // TODO: Detect blocks
    val packageName = PackageRegex.findFirstMatchIn(source).get.group(1)
    val caseClasses = detectCaseClasses(source)
    CodeFile(
      path = codeFile,
      source = source,
      packageName = packageName,
      caseClasses = caseClasses
    )
  }
}

case class CodeFile(path: Path, source: String, packageName: String, caseClasses: List[CaseClass]) {
  lazy val updatedSource: String = {
    var s = source
    caseClasses.foreach { cc =>
      val original = source.substring(cc.start, cc.end)
      s = s.replace(original, cc.code)
    }
    s
  }
}

case class CaseClass(className: String,
                     params: ListMap[String, Param],
                     mixIns: List[String],
                     start: Int,
                     end: Int,
                     updated: Boolean = false) {
  def update(): CaseClass = {
    val updatedParams = updateParams()
    val updatedMixIns = updateMixIns()
    if (updatedParams != params || updatedMixIns != mixIns) {
      copy(
        params = updatedParams,
        mixIns = updatedMixIns,
        updated = true
      )
    } else {
      this
    }
  }

  lazy val code: String = {
    val mixes = mixIns.zipWithIndex.map {
      case (className, index) if index == 0 => s" extends $className"
      case (className, _) => s" with $className"
    }.mkString
    s"case class $className(${params.values.map(_.toString).mkString(", ")})$mixes\n"
  }

  private def updateParams(): ListMap[String, Param] = {
    var p = params
    if (!p.contains("_id")) {
      p += "_id" -> Param("_id", s"Id[$className]", Some(s"$className.id()"))
    }
    p
  }

  private def updateMixIns(): List[String] = {
    var m = mixIns
    val doc = s"Document[$className]"
    if (!mixIns.contains(doc)) {
      m = m ::: List(doc)
    }
    m
  }
}

case class Param(name: String, `type`: String, default: Option[String]) {
  override def toString: String = s"$name: ${`type`}${default.map(d => s" = $d").getOrElse("")}"
}