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
libraryDependencies += "com.outr" %% "scarango-driver" % "3.7.1"
```

Or in Mill:

```
ivy"com.outr::scarango-driver:3.7.1"
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
import fabric.rw._

// Case class to represent a person collection
case class Person(name: String, age: Int, _id: Id[Person] = Person.id()) extends Document[Person]

// We use the companion object to represent additional information about the collection
object Person extends DocumentModel[Person] {
  override implicit val rw: RW[Person] = ccRW

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
Database.init()   // returns IO[Unit]
```

### Inserting into the database
A simple insert of a record into the database:
```scala
Database.people.insert(Person("User 1", 30))    // returns IO[CreateResult]
```
We can also do batch record insertion:
```scala
Database.people.batch.insert(List(
    Person("Adam", 21),
    Person("Bethany", 19)
))    // returns IO[CreateResults]
```

You can also use the `Database.people.stream` to cross-stream records into the database.

### Querying
In order to get the data out that we just inserted we can do a simple AQL query:
```scala
Database
    .people
    .query(aql"FOR p IN ${Database.people} RETURN p")
    .all    // returns IO[List[Person]]
```

For an example of data conversion in the result, if we want to only get the person's name back:
```scala
Database
  .people
  .query(aql"FOR p IN ${Database.people} RETURN p.name")
  .as[String]
  .all    // returns IO[List[String]]
```

For more examples see the specs: https://github.com/outr/scarango/blob/master/driver/src/test/scala/spec/GraphSpec.scala

## TODO
- Improved ScalaDocs
- Add AQL compile-time validation support (revive from 2.x)