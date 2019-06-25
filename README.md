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
libraryDependencies += "com.outr" %% "scarango-driver" % "2.0.0"
```

## Introduction

Scarango uses ArangoDB's HTTP end-points providing true asynchronous and non-blocking access to the database. We utilize
Scala `Future`s in order to handle asynchronous responses. This library has two main layers of abstraction. The first is
meant to be a bare-metal wrapper around the HTTP end-points to expose the maximum capabilities of the database for use.
The second layer is a higher level of abstraction to simplify management of the database while still providing flexiblity
and control. In the following examples we'll focus primarily on the second layer as it's generally the preferred. However,
for examples of the first layer take a look at the tests for simple and straight-forward examples of use.

## Getting Started

This needs to be updated with instructions for 2.0, but for now, take a look at 

## Versions

### Features for 2.1.0 (In-Progress)

* [ ] DSL for creating AQL queries
* [ ] Versioned Document functionality (replace and delete creates duplicate in another collection instead of updating)
* [ ] Scala.js wrapper for Foxx framework

### Features for 2.0.0 (Released 2019.06.25)

* [X] Generated integration with ArangoDB via Swagger
* [X] Scala.js core
* [X] Cleaner structure and functionality

### Features for 0.8.0 (Released 2017.08.31)

* [X] Support for sequences in AQL queries
* [X] Support for null in AQL queries
* [X] Support for Option in AQL queries
* [X] Support for Boolean in AQL queries
* [X] Support for BigDecimal in AQL queries

### Features for 0.7.0 (Released 2017.07.25)

* [X] Replace use of Typesafe Config with Profig for better support
* [X] Update driver for ArangoDB 3.2 changes
* [X] Test and update driver for RocksDB backing datastorage

### Features for 0.6.0 (Released 2017.06.23)

* [X] Seamless Re-Authentication support for token timeout

### Features for 0.5.0 (Released 2017.05.16)

* [X] Create Credentials support for better authentication paradigm
* [X] Support for Replication Logger (https://docs.arangodb.com/3.1/HTTP/Replications/ReplicationLogger.html)
* [X] Real-time change detection (upsert and deletion directly from the database) aka Triggers
* [X] `AbstractCollection.modify` feature to modify a document by supplying an original and modified case class only updating with the changes
* [X] Diff support for `modify` that properly handles null

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