package com.outr.arango.generator

import java.nio.file.{Files, Paths}
import scala.meta.inputs.Input
import scala.meta.{Defn, Import, Pkg, Source, Term, Tree}

/**
  * WARNING: This is a work-in-progress and not ready for use yet.
  */
object Generator {
  def main(args: Array[String]): Unit = {
    val personPath = Paths.get("..", "example", "src", "main", "scala", "example", "Person.scala")
    val input = Input.VirtualFile(personPath.toString, Files.readString(personPath))
    val source = input.parse[Source].get
    process(source)
  }

  private def process(tree: Tree): Unit = tree match {
    case s: Source => process(s.children.head)
    case p: Pkg => p.children.foreach(process)
    case t: Term.Name => scribe.info(s"Package? ${t.value}")
    case i: Import => scribe.info(s"Import: $i")
    case c: Defn.Class => scribe.info(s"Class: ${c.name} - $c")
    case o: Defn.Object => scribe.info(s"Object: ${o.name} - $o")
    case t: Defn.Trait => scribe.info(s"Trait: ${t.name} - $t")
    case _ => throw new RuntimeException(s"Unsupported: $tree (${tree.getClass.getName})")
  }
}