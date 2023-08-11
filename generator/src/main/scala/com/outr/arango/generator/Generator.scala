package com.outr.arango.generator

import java.nio.file.{Files, Path, Paths}
import scala.collection.immutable.ListMap
import scala.meta.inputs.Input
import scala.meta.{Defn, Import, Pkg, Source, Term, Tree}

/**
  * WARNING: This is a work-in-progress and not ready for use yet.
  */
object Generator {
  def main(args: Array[String]): Unit = {
    val personPath = Paths.get("..", "example", "src", "main", "scala", "example", "Person.scala")
    val generator = apply(personPath)
    generator.analyze()
  }

  def apply(path: Path): Generator = {
    val source = Files.readString(path)
    val input = Input.VirtualFile(path.toString, source)
    val tree = input.parse[Source].get

    var packages = List.empty[String]
    var imports = List.empty[String]
    var classes = ListMap.empty[String, Defn.Class]
    var objects = ListMap.empty[String, Defn.Object]
    var traits = ListMap.empty[String, Defn.Trait]

    def process(tree: Tree): Unit = tree match {
      case s: Source => process(s.children.head)
      case p: Pkg => p.children.foreach(process)
      case t: Term.Name => packages = t.value :: packages
      case i: Import => imports = i.toString() :: imports
      case c: Defn.Class => classes += c.name.toString() -> c
      case o: Defn.Object => objects += o.name.toString() -> o
      case t: Defn.Trait => traits += t.name.toString() -> t
      case _ => throw new RuntimeException(s"Unsupported: $tree (${tree.getClass.getName})")
    }

    process(tree)

    Generator(path, source, packages.reverse, imports.reverse, classes, objects, traits)
  }
}

case class Generator(path: Path,
                     source: String,
                     packages: List[String],
                     imports: List[String],
                     classes: ListMap[String, Defn.Class],
                     objects: ListMap[String, Defn.Object],
                     traits: ListMap[String, Defn.Trait]) {
  def analyze(): Unit = {
    classes.values.foreach { c =>
      scribe.info(s"Class: ${c.toString()} - ${c.ctor} - ${c.mods} - ${c.templ} - ${isDocumentClass(c)}")
      c.ctor.children.foreach { t =>
        scribe.info(s"Child: $t (${t.getClass.getName})")
      }
    }
  }

  private def isDocumentClass(c: Defn.Class): Boolean = c.templ.children.exists { t =>
    t.toString().contains(s"Document[${c.name}]")
  } || c.mods.exists(_.toString() == "case")
}