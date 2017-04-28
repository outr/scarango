# scarango

ArangoDB client written in Scala

## Features for 2.0.0 (Future)

* [ ] Real-time change detection (https://docs.arangodb.com/3.1/HTTP/Replications/ReplicationLogger.html)
* [ ] Scala.js wrapper for Foxx framework

## Features for 1.0.0 (In-Progress)

* [ ] DSL for creating AQL queries
* [ ] Versioned Document functionality (replace and delete creates duplicate in another collection instead of updating)

## Features for 0.3.0 (In-Progress)

* [X] Renaming of project from arangodb-scala to scarango
* [ ] Separation of core and driver for better re-use
* [ ] Better documentation and examples

## Features for 0.2.0 (Released 2017.04.28)

* [X] Higher level abstraction for working with documents
    * [X] Triggers (Before and After)
    * [X] Polymorphic Querying capabilities
    * [X] Convenience functionality for adding and managing edges
    
## Features for 0.1.0 (Released 2017.04.05)

* [X] Asynchronous client for all major HTTP end-points
* [X] Persist case classes
* [X] Query case classes
* [X] AQL compile-time validation