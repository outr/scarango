package com.outr.arango.api

import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import scala.concurrent.Future
import scribe.Execution.global
          
class ApiCollection{CollectionName}Get(client: HttpClient) {
  /**
  * The result is an object describing the collection with the following
  * attributes:
  * 
  * - *id*: The identifier of the collection.
  * 
  * - *name*: The name of the collection.
  * 
  * - *status*: The status of the collection as number.
  *  - 1: new born collection
  *  - 2: unloaded
  *  - 3: loaded
  *  - 4: in the process of being unloaded
  *  - 5: deleted
  *  - 6: loading
  * 
  * Every other status indicates a corrupted collection.
  * 
  * - *type*: The type of the collection as number.
  *   - 2: document collection (normal case)
  *   - 3: edges collection
  * 
  * - *isSystem*: If *true* then the collection is a system collection.
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * Accessing collections by their numeric ID is deprecated from version 3.4.0 on.
  * You should reference them via their names instead.
  * 
  * 
  * 
  * <!-- Hints End -->
  */
  def get(collectionName: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .params("collection-name" -> collection-name.toString)
    .call[ArangoResponse]
}