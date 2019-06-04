package com.outr.arango.api.model

import io.circe.Json

/**
  * HTTPAPITRAVERSAL
  *
  * @param startVertex id of the startVertex, e.g. {@literal *}"users/foo"{@literal *}.
  * @param direction direction for traversal
  *        - {@literal *}if set{@literal *}, must be either {@literal *}"outbound"{@literal *}, {@literal *}"inbound"{@literal *}, or {@literal *}"any"{@literal *}
  *        - {@literal *}if not set{@literal *}, the {@literal *}expander{@literal *} attribute must be specified
  * @param edgeCollection name of the collection that contains the edges.
  * @param expander body (JavaScript) code of custom expander function
  *        {@literal *}must{@literal *} be set if {@literal *}direction{@literal *} attribute is {@literal *}{@literal *}not{@literal *}{@literal *} set
  *        function signature: {@literal *}(config, vertex, path) -> array{@literal *}
  *        expander must return an array of the connections for {@literal *}vertex{@literal *}
  *        each connection is an object with the attributes {@literal *}edge{@literal *} and {@literal *}vertex{@literal *}
  * @param filter default is to include all nodes:
  *        body (JavaScript code) of custom filter function
  *        function signature: {@literal *}(config, vertex, path) -> mixed{@literal *}
  *        can return four different string values:
  *        - {@literal *}"exclude"{@literal *} -> this vertex will not be visited.
  *        - {@literal *}"prune"{@literal *} -> the edges of this vertex will not be followed.
  *        - {@literal *}""{@literal *} or {@literal *}undefined{@literal *} -> visit the vertex and follow its edges.
  *        - {@literal *}Array{@literal *} -> containing any combination of the above.
  *          If there is at least one {@literal *}"exclude"{@literal *} or {@literal *}"prune"{@literal *} respectively
  *          is contained, it's effect will occur.
  * @param graphName name of the graph that contains the edges.
  *        Either {@literal *}edgeCollection{@literal *} or {@literal *}graphName{@literal *} has to be given.
  *        In case both values are set the {@literal *}graphName{@literal *} is preferred.
  * @param init body (JavaScript) code of custom result initialization function
  *        function signature: {@literal *}(config, result) -> void{@literal *}
  *        initialize any values in result with what is required
  * @param itemOrder item iteration order can be {@literal *}"forward"{@literal *} or {@literal *}"backward"{@literal *}
  * @param maxDepth ANDed with any existing filters visits only nodes in at most the given depth
  * @param maxIterations Maximum number of iterations in each traversal. This number can be
  *        set to prevent endless loops in traversal of cyclic graphs. When a traversal performs
  *        as many iterations as the {@literal *}maxIterations{@literal *} value, the traversal will abort with an
  *        error. If {@literal *}maxIterations{@literal *} is not set, a server-defined value may be used.
  * @param minDepth ANDed with any existing filters):
  *        visits only nodes in at least the given depth
  * @param order traversal order can be {@literal *}"preorder"{@literal *}, {@literal *}"postorder"{@literal *} or {@literal *}"preorder-expander"{@literal *}
  * @param sort body (JavaScript) code of a custom comparison function
  *        for the edges. The signature of this function is
  *        {@literal *}(l, r) -> integer{@literal *} (where l and r are edges) and must
  *        return -1 if l is smaller than, +1 if l is greater than,
  *        and 0 if l and r are equal. The reason for this is the
  *        following: The order of edges returned for a certain
  *        vertex is undefined. This is because there is no natural
  *        order of edges for a vertex with multiple connected edges.
  *        To explicitly define the order in which edges on the
  *        vertex are followed, you can specify an edge comparator
  *        function with this attribute. Note that the value here has
  *        to be a string to conform to the JSON standard, which in
  *        turn is parsed as function body on the server side. Furthermore
  *        note that this attribute is only used for the standard
  *        expanders. If you use your custom expander you have to
  *        do the sorting yourself within the expander code.
  * @param strategy traversal strategy can be {@literal *}"depthfirst"{@literal *} or {@literal *}"breadthfirst"{@literal *}
  * @param uniqueness specifies uniqueness for vertices and edges visited.
  *        If set, must be an object like this:
  *        
  *        `"uniqueness": {"vertices": "none"|"global"|"path", "edges": "none"|"global"|"path"}`
  * @param visitor body (JavaScript) code of custom visitor function
  *        function signature: {@literal *}(config, result, vertex, path, connected) -> void{@literal *}
  *        The visitor function can do anything, but its return value is ignored. To
  *        populate a result, use the {@literal *}result{@literal *} variable by reference. Note that the
  *        {@literal *}connected{@literal *} argument is only populated when the {@literal *}order{@literal *} attribute is set
  *        to {@literal *}"preorder-expander"{@literal *}.
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class HTTPAPITRAVERSAL(startVertex: String,
                            direction: Option[String] = None,
                            edgeCollection: Option[String] = None,
                            expander: Option[String] = None,
                            filter: Option[String] = None,
                            graphName: Option[String] = None,
                            init: Option[String] = None,
                            itemOrder: Option[String] = None,
                            maxDepth: Option[String] = None,
                            maxIterations: Option[String] = None,
                            minDepth: Option[String] = None,
                            order: Option[String] = None,
                            sort: Option[String] = None,
                            strategy: Option[String] = None,
                            uniqueness: Option[String] = None,
                            visitor: Option[String] = None)