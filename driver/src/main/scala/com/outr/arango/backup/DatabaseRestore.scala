package com.outr.arango.backup

import cats.effect.IO
import cats.syntax.all._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.{Document, DocumentModel, Graph}
import fabric.io.{Format, JsonParser}

import java.nio.file.{Files, Path}
import scala.io.Source

object DatabaseRestore {
  trait AnyDoc extends Document[AnyDoc]

  def apply(graph: Graph,
            directory: Path,
            truncate: Boolean = false,
            upsert: Boolean = true): IO[Unit] = {
    graph.collections.flatMap { collection =>
      val file = directory.resolve(s"${collection.name}.collection")
      if (Files.exists(file)) {
        Some(restoreCollection(collection.asInstanceOf[DocumentCollection[AnyDoc, _ <: DocumentModel[AnyDoc]]], file, truncate, upsert))
      } else {
        None
      }
    }.sequence.map(_ => ())
  }

  def restoreCollection[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M],
                                                              path: Path,
                                                              truncate: Boolean,
                                                              upsert: Boolean): IO[Unit] = {
    val source = Source.fromFile(path.toFile)
    val pre = if (truncate) collection.collection.truncate() else IO.unit
    pre.flatMap { _ =>
      fs2.Stream.fromIterator[IO](source.getLines(), 1000).chunkN(1000).evalMap { chunk =>
        val batch = chunk.toList.map(_.trim).map(JsonParser.apply(_, Format.Json)).map(collection.model.rw.write)
        if (upsert) {
          collection.batch.upsert(batch)
        } else {
          collection.batch.insert(batch)
        }
      }.compile.drain.map { _ =>
        source.close()
      }
    }
  }
}