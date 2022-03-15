package com.outr.arango.backup

import cats.effect.IO
import cats.syntax.all._
import com.outr.arango.{Document, Graph}
import com.outr.arango.collection.DocumentCollection
import fabric.parse.{Json, JsonWriter}

import java.io.{FileWriter, PrintWriter}
import java.nio.file.{Files, Path}

object DatabaseBackup {
  def apply(graph: Graph, directory: Path): IO[Unit] = {
    Files.createDirectories(directory)
    graph.collections.map { collection =>
      val file = directory.resolve(s"${collection.name}.collection")
      backupCollection(collection, file)
    }.sequence.map(_ => ())
  }

  def backupCollection[D <: Document[D]](collection: DocumentCollection[D], file: Path): IO[Unit] = {
    val w = new PrintWriter(new FileWriter(file.toFile))
    collection
      .query
      .stream
      .foreach { value =>
        IO {
          val json = collection.model.rw.read(value)
          val jsonString = Json.format(json, JsonWriter.Compact)
          w.println(jsonString)
        }
      }
      .compile
      .drain
      .map { _ =>
        w.flush()
        w.close()
      }
  }
}