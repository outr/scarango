# Scarango

[![Build Status](https://travis-ci.org/outr/scarango.svg?branch=master)](https://travis-ci.org/outr/scarango)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/be829fed3c134f8cbf14c60290651d63)](https://www.codacy.com/app/matthicks/scarango?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=outr/scarango&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/be829fed3c134f8cbf14c60290651d63)](https://www.codacy.com/app/matthicks/scarango?utm_source=github.com&utm_medium=referral&utm_content=outr/scarango&utm_campaign=Badge_Coverage)
[![Stories in Ready](https://badge.waffle.io/outr/scarango.png?label=ready&title=Ready)](https://waffle.io/outr/scarango)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/outr/scarango)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.outr/scarango-driver_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.outr/scarango-driver_2.12)
[![Latest version](https://index.scala-lang.org/outr/scarango/scarango-driver/latest.svg)](https://index.scala-lang.org/outr/scarango)

ArangoDB client written in Scala

## Setup

Scarango is published to Sonatype OSS and Maven Central currently supporting Scala and Scala.js (core only) on 2.11 and 2.12.

Configuring the driver in SBT requires:

```
libraryDependencies += "com.outr" %% "scarango-driver" % "0.4.0"
```

## Dependencies

Bringing in any new library is always risky as it contains its own baggage of third-party dependencies. In Scarango we
try to keep the number of third-party dependencies to a minimum. Here are the main dependencies currently being used in
Scarango:

- Circe: JSON parsing and class encoding and decoding.
- Reactify: Functional reactive paradigms. This is mostly manifested in the triggers.
- Scribe: Logging framework.
- YouI: HTTP restful calls are managed through the HttpClient.

## Introduction

Scarango uses ArangoDB's HTTP end-points providing true asynchronous and non-blocking access to the database. We utilize
Scala `Future`s in order to handle asynchronous responses. This library has two main layers of abstraction. The first is
meant to be a bare-metal wrapper around the HTTP end-points to expose the maximum capabilities of the database for use.
The second layer is a higher level of abstraction to simplify management of the database while still providing flexiblity
and control. In the following examples we'll focus primarily on the second layer as it's generally the preferred. However,
for examples of the first layer take a look at the tests for simple and straight-forward examples of use.

## Getting Started

### Imports

For the basics of Scarango you'll need:

```
import com.outr.arango._
```

Because we're using the higher level abstraction we also need the `managed` package as well:

```
import com.outr.arango.managed._
```

### Case Classes

Scarango relies primarily on case classes to represent documents (vertex and edges), so we can easily map to and from the
database. We can extend from `DocumentOption` to access the extra information that an Arango document includes (_key, _id, and _rev):

```
case class Fruit(name: String,
                 _key: Option[String] = None,
                 _id: Option[String] = None,
                 _rev: Option[String] = None) extends DocumentOption
```

Notice that we define `_key`, `_id`, and `_rev` as `Option[String]` and default them to `None`. This allows them to be
populated by ArangoDB when they are inserted into the database. If you prefer to define the key yourself you may set the
`_key` value before insert and Arango will accept it accordingly.

### Graph and Collection

The next thing we need is a representation of our database. We can do this easily with our managed `Graph`:

```
object Database extends Graph("example") {
  val fruit: VertexCollection[Fruit] = vertex[Fruit]("fruit")
}
```

The code above creates a representation of our database, graph, and maps the `Fruit` class as a vertex collection in Arango.

The `Graph` class has default options for `db`, `url`, `username`, and `password` but can be set in the constructor as necessary.

### Initializing the Credentials

Now that we have our mapping representation of a database we need to initialize to verify credentials and get a token
that we can use for all communication to the database:

```
val future: Future[Boolean] = Database.init()
```

The result will be true if the credentials were accepted and no errors occurred.

### Creating the Database

Now that we're authenticated we need to create our graph and collection:

```
val future: Future[GraphResponse] = Database.fruit.create()
```

The `GraphResponse` contains a lot more information about what happened, but the primary thing to check in this situation
is `error` and making sure it's `false`.

### Inserting Fruit

We're finally ready to insert some content into our graph. Let's start with an Apple:

```
val future: Future[Fruit] = Database.fruit.insert(Fruit("Apple"))
```

Notice in the above we didn't include `_id`, `_key`, or `_rev` as these will be populated by the database. However, the
`Future[Fruit]` we get back will include all the values generated from the database.

### Querying with AQL

Scarango provides a compile-time validated AQL interpolator to give you proper compile-time errors if the query is invalid.

Let's create a query to get all the fruit back:

```
val query = aql"FOR f IN fruit RETURN f"
```

In order to use this query we can call `cursor` on the `fruit` collection:

```
val response: Future[QueryResponse[Fruit]] = Database.fruit.cursor(query)
```

The `QueryResponse` object has several useful pieces of information, but for our immediate needs calling `result` on it
will give us a `List[Fruit]` of the results of the query.

### Further Reading

For more examples using managed graphs take a look at the `ManagedSpec` (https://github.com/outr/scarango/blob/master/driver/src/test/scala/spec/ManagedSpec.scala).

## Versions

### Features for 2.0.0 (Future)

* [ ] Scala.js wrapper for Foxx framework
* [ ] Transactions

### Features for 1.0.0 (In-Progress)

* [ ] DSL for creating AQL queries
* [ ] Versioned Document functionality (replace and delete creates duplicate in another collection instead of updating)
* [ ] Seamless Re-Authentication support for token timeout

### Features for 0.5.0 (In-Progress)

* [ ] Create Credentials support for better authentication paradigm
* [ ] Real-time change detection (https://docs.arangodb.com/3.1/HTTP/Replications/ReplicationLogger.html)

### Features for 0.4.0 (Released 2017.05.10)

* [X] Support for passing collection as reference in AQL interpolation
* [X] AQL `execute` convenience method for no results
* [X] AQL `call` convenience method for exactly one result
* [X] AQL `first` convenience method for optional first result
* [X] Complete Indexing support
* [X] Additional functionality for key/value collection (Map implementation)
* [X] Upsert functionality convenience functionality
* [X] Graph knowledge of all collections and `Graph.init` can optionally create all missing collections
* [X] Trigger based `modified` updates
* [X] Database Upgrade infrastructure
* [X] QueryResponsePagination to easily page through results
* [X] QueryResponseIterator to cleanly iterate over every result without loading everything into memory
* [X] Support ArangoDB with authentication disabled
* [X] Support AbstractCollection.replace by key to allow updating the document's key
* [X] Add support for Arango default configuration to be loaded optionally from typesafe.config

### Features for 0.3.0 (Release 2017.04.28)

* [X] Renaming of project from arangodb-scala to scarango
* [X] Separation of core and driver for better re-use
* [X] Better documentation and examples

### Features for 0.2.0 (Released 2017.04.28)

* [X] Higher level abstraction for working with documents
    * [X] Triggers (Before and After)
    * [X] Polymorphic Querying capabilities
    * [X] Convenience functionality for adding and managing edges
    
### Features for 0.1.0 (Released 2017.04.05)

* [X] Asynchronous client for all major HTTP end-points
* [X] Persist case classes
* [X] Query case classes
* [X] AQL compile-time validation