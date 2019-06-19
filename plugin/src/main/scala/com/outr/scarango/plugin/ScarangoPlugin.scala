package com.outr.scarango.plugin

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{Files, Path}

import sbt.{file, _}
import Keys._

import scala.collection.JavaConverters._
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader

object ScarangoPlugin extends sbt.AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val updateModels = TaskKey[Unit]("updateModels", "Creates or updates DocumentModel companions for Document case classes")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    updateModels := {
      val classPath = fullClasspath.in(Runtime).value
      val urls = classPath.map(_.data.toURI.toURL)
      val classLoader = new URLClassLoader(urls, null)
      val dir = classPath.head.data.toPath
      val documentTrait = classLoader.loadClass("com.outr.arango.Document")
      val classes: List[String] = Files.find(dir, Int.MaxValue, (path: Path, _: BasicFileAttributes) => {
        path.toString.toLowerCase.endsWith(".class")
      })
        .iterator()
        .asScala
        .toList
        .map(dir.relativize)
        .map(_.toString.replace('/', '.'))
        .map(c => c.substring(0, c.length - 6))
        .filterNot(_.endsWith("$"))
        .distinct
      classes.foreach { c =>
        val clazz = classLoader.loadClass(c)
        val isDocument = documentTrait.isAssignableFrom(clazz)
        if (isDocument && !clazz.isInterface) {
          import scala.reflect.runtime.universe._

          val m = runtimeMirror(classLoader)

          // TODO: support recursively built fields list, breaking into sub-objects supporting Option[case class] and Seq[case class]
          val classSymbol = m.classSymbol(clazz)
          val classMirror = m.reflectClass(classSymbol)
          val moduleSymbol = classMirror.symbol.companion
          val apply = moduleSymbol
            .typeSignature
            .decls
            .filter(_.isMethod)
            .filter(_.asMethod.name.toTermName == TermName("apply"))
            .map(_.asMethod)
            .last
          println(s"Updating model for $clazz")
          val paths = sourceDirectories.in(Runtime).value
          val directories = paths.map(p => new File(p, clazz.getPackage.getName.replace('.', '/')))
          directories.map(new File(_, s"${clazz.getSimpleName}.scala")).find(_.exists()) match {
            case Some(file) => {
              val source = IO.read(file)
              val obj = extractObject(clazz.getSimpleName, source)
              var modified = obj

              // Remove all fields
              val FieldRegex = """val (.+): Field\[(.+?)\] = Field\[(.+?)\]\("(.+)"\)\n""".r
              modified = FieldRegex.replaceAllIn(modified, "")

              // Generate fields
              val params = apply.paramLists.head.map(_.asTerm)
              val fields = params.map { p =>
                val `type` = p.typeSignature.resultType.toString.replaceAllLiterally("Predef.", "")
                object CaseField {
                  def unapply(trmSym: TermSymbol): Option[(Name, Type)] = {
                    if (trmSym.isVal && trmSym.isCaseAccessor)
                      Some((TermName(trmSym.name.toString.trim), trmSym.typeSignature))
                    else
                      None
                  }
                }
                val caseEntries = p.typeSignature.resultType.decls.collect {
                  case CaseField(n, tpe) => (n, tpe)
                }.toList
                val name = p.name.decodedName.toString
                val encName = encodedName(name)
                if (caseEntries.isEmpty || `type`.startsWith("com.outr.arango.Id[")) {
                  val n = name match {
                    case "_identity" => "_id"
                    case _ => name
                  }
                  s"""val $encName: Field[${`type`}] = Field[${`type`}]("$n")"""
                } else {
                  val subFields = caseEntries.map {
                    case (n, tpe) => {
                      val subType = tpe.toString.replaceAllLiterally("Predef.", "")
                      s"""val ${encodedName(n.decodedName.toString)}: Field[$subType] = Field[$subType]("$name.$n")"""
                    }
                  }
                  s"""object $encName extends Field[${`type`}]("$name") {
                     |    ${subFields.mkString("\n    ")}
                     |  }""".stripMargin
                }
              }
              val openIndex = modified.indexOf('{')
              val prefix = modified.substring(0, openIndex + 1)
              val postfix = modified.substring(openIndex + 1)
              modified = s"$prefix${fields.map(f => s"\n  $f").mkString}$postfix"

              val hasImport = "import com[.]outr[.]arango[.].*?Field".r.findFirstIn(source).nonEmpty ||
                "import com[.]outr[.]arango[.]_".r.findFirstIn(source).nonEmpty

              var modifiedSource = source
              modifiedSource = modifiedSource.replaceAllLiterally(obj, modified)

              if (!hasImport) {
                val index = modifiedSource.indexOf('\n')
                val pre = modifiedSource.substring(0, index + 1)
                val post = modifiedSource.substring(index + 1)
                modifiedSource =
                  s"""$pre
                     |import com.outr.arango.Field
                     |$post
                   """.stripMargin
              }

              IO.write(file, modifiedSource.trim.getBytes)
            }
            case None => println(s"No file found for $clazz")
          }
        }
      }
    }
  )

  private def encodedName(name: String): String = name match {
    case "type" => "`type`"
    case _ => name
  }

  private def extractObject(className: String, source: String): String = {
    val start = source.indexOf(s"object $className")
    val b = new StringBuilder
    var open = List.empty[Char]
    var started = false
    var ended = false
    source.substring(start).foreach { c =>
      if (!ended) {
        if (c == '"') {
          if (open.headOption.contains('"')) {
            open = open.tail
          } else {
            open = c :: open
          }
        } else if (c == '{' && !open.headOption.contains('"')) {
          started = true
          open = c :: open
        } else if (c == '}' && !open.headOption.contains('"')) {
          open = open.tail
          if (open.isEmpty) ended = true
        }
        b.append(c)
      }
    }
    b.toString()
  }
}

case class ModelDetails(className: String, args: List[ModelArg], path: Path, packageName: String) {
  override def toString: String = s"$className(${args.mkString(", ")})"
}

case class ModelArg(name: String, `type`: String) {
  override def toString: String = s"$name: ${`type`}"
}