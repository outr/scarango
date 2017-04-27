# arangodb-scala

ArangoDB client written in Scala

## Features for 1.0.0 (In-Progress)

* [X] Asynchronous client for all major HTTP end-points
* [X] Persist case classes
* [X] Query case classes
* [X] AQL compile-time validation
* [ ] DSL for creating AQL queries
* [ ] Higher level abstraction of Document
    * [X] Triggers (Before and After)
    * [X] Polymorphic Querying capabilities
    * [X] Convenience functionality for adding and managing edges
    * [ ] Versioned Document functionality (replace and delete creates duplicate in another collection instead of updating)
