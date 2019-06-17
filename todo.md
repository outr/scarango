- [ ] View creation support
- [ ] ArangoSearch creation support
- [ ] Test traversal in search
    - ```text
    LET traversalDepth = 1..5
    FOR startVertex IN myView SEARCH <your-search-condition>
    SORT BM25(startVertex) DESC // optional
    LIMIT 1000 // optional
    FOR v,e,p IN traversalDepth OUTBOUND startVertex myEdgeCollection
    <your-traversal-logic>
    RETURN <your-return-value>
    ```
    
    or
    
    ```
    WITH Image
    FOR doc, e IN 1..1 OUTBOUND @fromId LinkedImage
       FILTER IS_DOCUMENT(doc)
       FILTER LIKE(e.role, "hero") // CONTAINS, REGEX_MATCHES
       RETURN doc
   ```
   
   or
   
   FOR d IN myView SEARCH <your-search-condition>
   FOR v,e,p IN 1..1 OUTPUT <your-traversal-logic>
   RETURN <your-output>
   
   1.) Solr individual indexes
   2.) MongoDB aggregation
   3.) Don't allow fulltext search in the first release
   4.) Solr arrays of credentials applied to messages
   5.) Evaluate OrientDB which is a 100% Java graph database that allows indexing with Lucene
   6.) Use ArangoDB + TEXT functions instead of fulltext (ex. `FILTER LIKE(text, "criteria")`)