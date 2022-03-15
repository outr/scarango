package com.outr.arango.backup

import cats.effect.IO
import cats.syntax.all._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.{Document, Graph}
import fabric.parse.Json

import java.nio.file.{Files, Path}
import scala.io.Source

object DatabaseRestore {
  def apply(graph: Graph,
            directory: Path,
            truncate: Boolean = false,
            upsert: Boolean = true): IO[Unit] = {
    graph.collections.flatMap { collection =>
      val file = directory.resolve(s"${collection.name}.collection")
      if (Files.exists(file)) {
        Some(restoreCollection(collection, file, truncate, upsert))
      } else {
        None
      }
    }.sequence.map(_ => ())
  }

  def restoreCollection[D <: Document[D]](collection: DocumentCollection[D],
                                          path: Path,
                                          truncate: Boolean,
                                          upsert: Boolean): IO[Unit] = {
    val source = Source.fromFile(path.toFile)
    val pre = if (truncate) collection.collection.truncate() else IO.unit
    pre.flatMap { _ =>
      fs2.Stream.fromIterator[IO](source.getLines(), 1000).chunkN(1000).evalMap { chunk =>
        val batch = chunk.toList.map(_.trim).map(Json.parse).map(collection.model.rw.write)
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