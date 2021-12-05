# Scarango

[![CI](https://github.com/outr/scarango/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/outr/scarango/actions/workflows/ci.yml)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/outr/scarango)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.outr/scarango-driver_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.outr/scarango-driver_2.13)
[![Latest version](https://index.scala-lang.org/outr/scarango/scarango-driver/latest.svg)](https://index.scala-lang.org/outr/scarango)

ArangoDB client written in Scala

## Setup

Scarango is published to Sonatype OSS and Maven Central currently supporting Scala and Scala.js (core only) on 2.12 and 2.13.

Configuring the driver in SBT requires:

```
libraryDependencies += "com.outr" %% "scarango-driver" % "2.4.3"
```

## Introduction

Scarango uses ArangoDB's HTTP end-points providing true asynchronous and non-blocking access to the database. We utilize
Scala `Future`s in order to handle asynchronous responses. This library has two main layers of abstraction. The first is
meant to be a bare-metal wrapper around the HTTP end-points to expose the maximum capabilities of the database for use.
The second layer is a higher level of abstraction to simplify management of the database while still providing flexiblity
and control. In the following examples we'll focus primarily on the second layer as it's generally the preferred. However,
for examples of the first layer take a look at the tests for simple and straight-forward examples of use.

## Getting Started

This needs to be updated with instructions for 2.0, but for now, take a look at https://github.com/outr/scarango/blob/master/driver/src/test/scala/spec/GraphSpec.scala

## TODO for 3.0 release
- Evaluate open TODO items
- ScalaDocs

## TODO for 3.1 release
- Backup / Restore features
- Add Streaming Update support: collection.stream.update(query) { value => UpdateResult.{Unchanged, Replace, Delete, Insert}}
- Getting Started documentation
- AQL validation support