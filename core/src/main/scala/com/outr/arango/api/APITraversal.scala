package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APITraversal(client: HttpClient) {
  /**
  * Starts a traversal starting from a given vertex and following.
  * edges contained in a given edgeCollection. The request must
  * contain the following attributes.
  * 
  * 
  * **A JSON object with these properties is required:**
  * 
  *   - **sort**: body (JavaScript) code of a custom comparison function
  *    for the edges. The signature of this function is
  *    *(l, r) -> integer* (where l and r are edges) and must
  *    return -1 if l is smaller than, +1 if l is greater than,
  *    and 0 if l and r are equal. The reason for this is the
  *    following: The order of edges returned for a certain
  *    vertex is undefined. This is because there is no natural
  *    order of edges for a vertex with multiple connected edges.
  *    To explicitly define the order in which edges on the
  *    vertex are followed, you can specify an edge comparator
  *    function with this attribute. Note that the value here has
  *    to be a string to conform to the JSON standard, which in
  *    turn is parsed as function body on the server side. Furthermore
  *    note that this attribute is only used for the standard
  *    expanders. If you use your custom expander you have to
  *    do the sorting yourself within the expander code.
  *   - **direction**: direction for traversal
  *    - *if set*, must be either *"outbound"*, *"inbound"*, or *"any"*
  *    - *if not set*, the *expander* attribute must be specified
  *   - **minDepth**: ANDed with any existing filters):
  *    visits only nodes in at least the given depth
  *   - **startVertex**: id of the startVertex, e.g. *"users/foo"*.
  *   - **visitor**: body (JavaScript) code of custom visitor function
  *    function signature: *(config, result, vertex, path, connected) -> void*
  *    The visitor function can do anything, but its return value is ignored. To
  *    populate a result, use the *result* variable by reference. Note that the
  *    *connected* argument is only populated when the *order* attribute is set
  *    to *"preorder-expander"*.
  *   - **itemOrder**: item iteration order can be *"forward"* or *"backward"*
  *   - **strategy**: traversal strategy can be *"depthfirst"* or *"breadthfirst"*
  *   - **filter**: default is to include all nodes:
  *    body (JavaScript code) of custom filter function
  *    function signature: *(config, vertex, path) -> mixed*
  *    can return four different string values:
  *    - *"exclude"* -> this vertex will not be visited.
  *    - *"prune"* -> the edges of this vertex will not be followed.
  *    - *""* or *undefined* -> visit the vertex and follow its edges.
  *    - *Array* -> containing any combination of the above.
  *      If there is at least one *"exclude"* or *"prune"* respectively
  *      is contained, it's effect will occur.
  *   - **init**: body (JavaScript) code of custom result initialization function
  *    function signature: *(config, result) -> void*
  *    initialize any values in result with what is required
  *   - **maxIterations**: Maximum number of iterations in each traversal. This number can be
  *    set to prevent endless loops in traversal of cyclic graphs. When a traversal performs
  *    as many iterations as the *maxIterations* value, the traversal will abort with an
  *    error. If *maxIterations* is not set, a server-defined value may be used.
  *   - **maxDepth**: ANDed with any existing filters visits only nodes in at most the given depth
  *   - **uniqueness**: specifies uniqueness for vertices and edges visited.
  *    If set, must be an object like this:
  *    `"uniqueness": {"vertices": "none"|"global"|"path", "edges": "none"|"global"|"path"}`
  *   - **order**: traversal order can be *"preorder"*, *"postorder"* or *"preorder-expander"*
  *   - **graphName**: name of the graph that contains the edges.
  *    Either *edgeCollection* or *graphName* has to be given.
  *    In case both values are set the *graphName* is preferred.
  *   - **expander**: body (JavaScript) code of custom expander function
  *    *must* be set if *direction* attribute is **not** set
  *    function signature: *(config, vertex, path) -> array*
  *    expander must return an array of the connections for *vertex*
  *    each connection is an object with the attributes *edge* and *vertex*
  *   - **edgeCollection**: name of the collection that contains the edges.
  * 
  * 
  * 
  * 
  * 
  * If the Traversal is successfully executed *HTTP 200* will be returned.
  * Additionally the *result* object will be returned by the traversal.
  * 
  * For successful traversals, the returned JSON object has the
  * following properties:
  * 
  * - *error*: boolean flag to indicate if an error occurred (*false*
  *   in this case)
  * 
  * - *code*: the HTTP status code
  * 
  * - *result*: the return value of the traversal
  * 
  * If the traversal specification is either missing or malformed, the server
  * will respond with *HTTP 400*.
  * 
  * The body of the response will then contain a JSON object with additional error
  * details. The object has the following attributes:
  * 
  * - *error*: boolean flag to indicate that an error occurred (*true* in this case)
  * 
  * - *code*: the HTTP status code
  * 
  * - *errorNum*: the server error number
  * 
  * - *errorMessage*: a descriptive error message
  * 
  * 
  * <!-- Hints Start -->
  * 
  * **Warning:**  
  * This route should no longer be used.
  * It is considered as deprecated from version 3.4.0 on.
  * It is superseded by AQL graph traversal.
  * 
  * 
  * 
  * <!-- Hints End -->
  * 
  * 
  * **Example:**
  *  In the following examples the underlying graph will contain five persons
  * *Alice*, *Bob*, *Charlie*, *Dave* and *Eve*.
  * We will have the following directed relations:
  * 
  * - *Alice* knows *Bob*
  * - *Bob* knows *Charlie*
  * - *Bob* knows *Dave*
  * - *Eve* knows *Alice*
  * - *Eve* knows *Bob*
  * 
  * The starting vertex will always be Alice.
  * 
  * Follow only outbound edges
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--F"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106915"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106915"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YX2--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106915"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106915"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YX2--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106919"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106919"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YX2--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106915"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106915"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YX2--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106922"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106922"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YX2--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YXy--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Follow only inbound edges
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"inbound"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YN2--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YN6--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YN2--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106613"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106613"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YN6--L"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YN2--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YN6--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Follow any direction of edges
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"any"</span>, 
  * </code><code>  <span class="hljs-string">"uniqueness"</span> : { 
  * </code><code>    <span class="hljs-string">"vertices"</span> : <span class="hljs-string">"none"</span>, 
  * </code><code>    <span class="hljs-string">"edges"</span> : <span class="hljs-string">"global"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--F"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--H"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106096"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106096"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106096"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106096"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106100"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106100"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106096"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106096"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106103"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106103"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106096"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106096"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106109"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106109"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--H"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106096"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106096"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106109"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106109"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106106"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106106"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_K--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--H"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Y_G--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Excluding *Charlie* and *Bob*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span>, 
  * </code><code>  <span class="hljs-string">"filter"</span> : <span class="hljs-string">"if (vertex.name === \"Bob\" ||     vertex.name === \"Charlie\") {  return \"exclude\";}return;"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJi--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJm--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106462"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106462"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJm--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106469"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106469"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJm--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJi--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YJm--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Do not follow edges from *Bob*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span>, 
  * </code><code>  <span class="hljs-string">"filter"</span> : <span class="hljs-string">"if (vertex.name === \"Bob\") {return \"prune\";}return;"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YLu--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YLu--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YLu--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106535"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106535"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YLy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YLu--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YLu--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Visit only nodes in a depth of at least 2
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span>, 
  * </code><code>  <span class="hljs-string">"minDepth"</span> : <span class="hljs-number">2</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--F"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106842"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106842"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUS--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106846"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106846"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUS--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106842"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106842"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUS--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106849"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106849"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUS--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YUO--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Visit only nodes in a depth of at most 1
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span>, 
  * </code><code>  <span class="hljs-string">"maxDepth"</span> : <span class="hljs-number">1</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YQ---_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YQ---B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YQ---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106672"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106672"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YQC--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YQ---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YQ---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using a visitor function to return vertex ids only
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span>, 
  * </code><code>  <span class="hljs-string">"visitor"</span> : <span class="hljs-string">"result.visited.vertices.push(vertex._id);"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>        <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>        <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>        <span class="hljs-string">"persons/dave"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Count all visited nodes and return a list of nodes only
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"outbound"</span>, 
  * </code><code>  <span class="hljs-string">"init"</span> : <span class="hljs-string">"result.visited = 0; result.myVertices = [ ];"</span>, 
  * </code><code>  <span class="hljs-string">"visitor"</span> : <span class="hljs-string">"result.visited++; result.myVertices.push(vertex);"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : <span class="hljs-number">4</span>, 
  * </code><code>    <span class="hljs-string">"myVertices"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>        <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>        <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yci--_"</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>        <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>        <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ycm--_"</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>        <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>        <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ycm--B"</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>        <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>        <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ycm--D"</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>      } 
  * </code><code>    ] 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Expand only inbound edges of *Alice* and outbound edges of *Eve*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"expander"</span> : <span class="hljs-string">"var connections = [ ];if (vertex.name === \"Alice\") {config.datasource.getInEdges(vertex).forEach(function (e) {connections.push({ vertex: require(\"internal\").db._document(e._from), edge: e});});}if (vertex.name === \"Eve\") {config.datasource.getOutEdges(vertex).forEach(function (e) {connections.push({vertex: require(\"internal\").db._document(e._to), edge: e});});}return connections;"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeq--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeu--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeq--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107170"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107170"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeu--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeu--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107170"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107170"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeu--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107173"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107173"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yey--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeu--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Yeq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Follow the *depthfirst* strategy
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"any"</span>, 
  * </code><code>  <span class="hljs-string">"strategy"</span> : <span class="hljs-string">"depthfirst"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--F"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--F"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106279"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106279"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106279"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106279"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106283"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106283"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106279"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106279"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106286"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106286"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106279"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106279"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106292"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106292"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106279"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106279"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106292"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106292"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106289"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106289"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106289"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106289"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106289"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106289"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106292"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106292"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106289"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106289"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106292"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106292"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106283"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106283"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106289"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106289"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106292"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106292"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106286"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106286"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106289"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106289"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106292"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106292"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106279"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106279"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEi--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YEe--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using *postorder* ordering
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"any"</span>, 
  * </code><code>  <span class="hljs-string">"order"</span> : <span class="hljs-string">"postorder"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106988"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106988"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106992"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106992"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106988"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106988"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106995"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106995"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106988"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106988"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107001"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107001"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106998"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106998"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106988"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106988"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107001"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107001"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106988"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106988"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106998"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106998"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107001"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107001"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106992"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106992"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106998"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106998"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107001"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107001"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106995"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106995"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--J"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106998"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106998"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107001"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107001"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106988"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106988"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106998"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106998"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"107001"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/107001"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106998"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106998"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaG--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YaC--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1Ya---_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Using *backward* item-ordering:
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"any"</span>, 
  * </code><code>  <span class="hljs-string">"itemOrder"</span> : <span class="hljs-string">"backward"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--A"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu---"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--A"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu---"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106190"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106190"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106190"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106190"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106193"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106193"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106190"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106190"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106193"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106193"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106180"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106180"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--E"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106190"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106190"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106193"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106193"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106187"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106187"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--A"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106190"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106190"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106193"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106193"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106184"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106184"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--G"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu---"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106180"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106180"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--E"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106180"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106180"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--E"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106193"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106193"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106180"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106180"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--E"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106193"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106193"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106190"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106190"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--C"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106180"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106180"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--E"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106187"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106187"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBy--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--A"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106180"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106180"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--E"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106184"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106184"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu--G"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBq--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YBu---"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Edges should only be included once globally,
  * but nodes are included every time they are visited
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"any"</span>, 
  * </code><code>  <span class="hljs-string">"uniqueness"</span> : { 
  * </code><code>    <span class="hljs-string">"vertices"</span> : <span class="hljs-string">"none"</span>, 
  * </code><code>    <span class="hljs-string">"edges"</span> : <span class="hljs-string">"global"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"result"</span> : { 
  * </code><code>    <span class="hljs-string">"visited"</span> : { 
  * </code><code>      <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--B"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--D"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--F"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>          <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>          <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"paths"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106378"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106378"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106378"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106378"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106382"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106382"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--B"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/charlie"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--B"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Charlie"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106378"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106378"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106385"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106385"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--D"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"bob"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"dave"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/dave"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--D"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Dave"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106378"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106378"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106391"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106391"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"edges"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106378"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106378"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--_"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106391"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106391"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--H"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"106388"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"knows/106388"</span>, 
  * </code><code>              <span class="hljs-string">"_from"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_to"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHO--F"</span>, 
  * </code><code>              <span class="hljs-string">"vertex"</span> : <span class="hljs-string">"eve"</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"vertices"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"bob"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/bob"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Bob"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"eve"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/eve"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHK--F"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Eve"</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"_key"</span> : <span class="hljs-string">"alice"</span>, 
  * </code><code>              <span class="hljs-string">"_id"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>              <span class="hljs-string">"_rev"</span> : <span class="hljs-string">"_YOn1YHG--_"</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"Alice"</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        } 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  If the underlying graph is cyclic, *maxIterations* should be set
  * 
  * The underlying graph has two vertices *Alice* and *Bob*.
  * With the directed edges:
  * 
  * - *Alice* knows *Bob*
  * - *Bob* knows *Alice*
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/traversal</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"startVertex"</span> : <span class="hljs-string">"persons/alice"</span>, 
  * </code><code>  <span class="hljs-string">"graphName"</span> : <span class="hljs-string">"knows_graph"</span>, 
  * </code><code>  <span class="hljs-string">"direction"</span> : <span class="hljs-string">"any"</span>, 
  * </code><code>  <span class="hljs-string">"uniqueness"</span> : { 
  * </code><code>    <span class="hljs-string">"vertices"</span> : <span class="hljs-string">"none"</span>, 
  * </code><code>    <span class="hljs-string">"edges"</span> : <span class="hljs-string">"none"</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"maxIterations"</span> : <span class="hljs-number">5</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Internal Server <span class="hljs-built_in">Error</span>
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">500</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1909</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"too many iterations - try increasing the value of 'maxIterations'"</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: HTTPAPITRAVERSAL): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/traversal".withArguments(Map()))
    .restful[HTTPAPITRAVERSAL, ArangoResponse](body)
}