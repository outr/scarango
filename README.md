# Scarango

[![CI](https://github.com/outr/scarango/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/outr/scarango/actions/workflows/ci.yml)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/outr/scarango)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.outr/scarango-driver_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.outr/scarango-driver_2.13)
[![Latest version](https://index.scala-lang.org/outr/scarango/scarango-driver/latest.svg)](https://index.scala-lang.org/outr/scarango)

ArangoDB client written in Scala

## Setup

Scarango is published to Sonatype OSS and Maven Central currently supporting Scala and Scala.js (core only) on 2.13 and 3.

Configuring the driver in SBT requires:

```
libraryDependencies += "com.outr" %% "scarango-driver" % "3.8.2"
```

Or in Mill:

```
ivy"com.outr::scarango-driver:3.8.2"
```

## Introduction

Scarango wraps ArangoDB's Java library to provide lots of additional features and Scala-specific functionality. We utilize
cats-effect and fs2 for a more modern asynchronous approach. Previous to 3.0, we utilized direct HTTP RESTful calls and
Futures, but the performance benefits of Java's library made a migration worthwhile.

## Getting Started

Although there are a few different ways we can utilize Scarango to interact with ArangoDB, the cleanest and most powerful
approach is to utilize the Graph layer to set up our database structure and interact with it in a type-safe way. For example:

### Database configuration
```scala
import com.outr.arango.{Document, DocumentModel, Field, Graph, Id, Index}
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query._
import fabric.rw._
import cats.effect.unsafe.implicits.global

// Case class to represent a person collection
case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

// We use the companion object to represent additional information about the collection
object Person extends DocumentModel[Person] {
  override implicit val rw: RW[Person] = RW.gen

  val name: Field[String] = field("name")
  val age: Field[Int] = field("age")

  override def indexes: List[Index] = List(
    name.index.persistent()
  )

  override val collectionName: String = "people"
}

// We represent our entire database here referencing all collections
object Database extends Graph("example") {
  val people: DocumentCollection[Person] = vertex[Person](Person)
}
```
This is the basic setup of a single-collection database. Notice the `RW` in the companion object. That is defined
using [Fabric](https://github.com/outr/fabric) for conversion to/from JSON for storage in the database. All fields aren't
required to be defined, but it will help us when we want to write simple queries in a type-safe way or when defining things
like indexes.

### Initialization
The next thing we need to do is initialize the database:
```scala
Database.init().unsafeRunSync()
```

NOTE: We're adding `.unsafeRunSync()` at the end of each call for the documentation generation to get the result. Under
normal circumstances this is not the ideal way to execute a cats-effect `IO`.

### Truncate the database
We can easily clear out everything out of the database:
```scala
Database.truncate().unsafeRunSync()
```

### Inserting into the database
A simple insert of a record into the database:
```scala
Database.people.insert(Person("User 1", 30)).unsafeRunSync()
// res2: com.outr.arango.core.CreateResult[Person] = CreateResult(
//   key = None,
//   id = None,
//   rev = None,
//   newDocument = None,
//   oldDocument = None
// )
```
We can also do batch record insertion:
```scala
Database.people.batch.insert(List(
    Person("Adam", 21),
    Person("Bethany", 19)
)).unsafeRunSync()
// res3: com.outr.arango.core.CreateResults[Person] = CreateResults(
//   results = List()
// )
```

You can also use the `Database.people.stream` to cross-stream records into the database.

### Querying
In order to get the data out that we just inserted we can do a simple AQL query:
```scala
Database
  .people
  .query(aql"FOR p IN ${Database.people} RETURN p")
  .all
  .unsafeRunSync()
// res4: List[Person] = List(
//   Person(
//     name = "User 1",
//     age = 30,
//     _id = Id(value = "fsUrM4CFxwwKwXwpLYZvUjul5uB6h68h", collection = "people")
//   ),
//   Person(
//     name = "Adam",
//     age = 21,
//     _id = Id(value = "wlqEc2CWnWdh5Bg0drZUZKoEr0CvrCmn", collection = "people")
//   ),
//   Person(
//     name = "Bethany",
//     age = 19,
//     _id = Id(value = "vPQZXqj0abHso0Q58wjSFbmzf0XJmfb6", collection = "people")
//   )
// )
```

For an example of data conversion in the result, if we want to only get the person's name back:
```scala
Database
  .people
  .query(aql"FOR p IN ${Database.people} RETURN p.name")
  .as[String]
  .all
  .unsafeRunSync()
// res5: List[String] = List("Adam", "Bethany", "User 1")
```

For more examples see the specs: https://github.com/outr/scarango/blob/master/driver/src/test/scala/spec/GraphSpec.scala

## TODO
- Improved ScalaDocs
- Add AQL compile-time validation support (revive from 2.x)