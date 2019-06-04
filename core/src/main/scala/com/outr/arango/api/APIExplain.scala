package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIExplain(client: HttpClient) {
  /**
  * **A JSON object with these properties is required:**
  * 
  *   - **query**: the query which you want explained; If the query references any bind variables,
  *    these must also be passed in the attribute *bindVars*. Additional
  *    options for the query can be passed in the *options* attribute.
  *   - **options**:
  *     - **optimizer.rules** (string): an array of to-be-included or to-be-excluded optimizer rules
  *     can be put into this attribute, telling the optimizer to include or exclude
  *     specific rules. To disable a rule, prefix its name with a `-`, to enable a rule, prefix it
  *     with a `+`. There is also a pseudo-rule `all`, which will match all optimizer rules.
  *     - **maxNumberOfPlans**: an optional maximum number of plans that the optimizer is 
  *     allowed to generate. Setting this attribute to a low value allows to put a
  *     cap on the amount of work the optimizer does.
  *     - **allPlans**: if set to *true*, all possible execution plans will be returned.
  *     The default is *false*, meaning only the optimal plan will be returned.
  *   - **bindVars** (object): key/value pairs representing the bind parameters.
  * 
  * 
  * 
  * 
  * 
  * To explain how an AQL query would be executed on the server, the query string
  * can be sent to the server via an HTTP POST request. The server will then validate
  * the query and create an execution plan for it. The execution plan will be
  * returned, but the query will not be executed.
  * 
  * The execution plan that is returned by the server can be used to estimate the
  * probable performance of the query. Though the actual performance will depend
  * on many different factors, the execution plan normally can provide some rough
  * estimates on the amount of work the server needs to do in order to actually run 
  * the query.
  * 
  * By default, the explain operation will return the optimal plan as chosen by
  * the query optimizer The optimal plan is the plan with the lowest total estimated
  * cost. The plan will be returned in the attribute *plan* of the response object.
  * If the option *allPlans* is specified in the request, the result will contain 
  * all plans created by the optimizer. The plans will then be returned in the 
  * attribute *plans*.
  * 
  * The result will also contain an attribute *warnings*, which is an array of 
  * warnings that occurred during optimization or execution plan creation. Additionally,
  * a *stats* attribute is contained in the result with some optimizer statistics.
  * If *allPlans* is set to *false*, the result will contain an attribute *cacheable* 
  * that states whether the query results can be cached on the server if the query
  * result cache were used. The *cacheable* attribute is not present when *allPlans*
  * is set to *true*.
  * 
  * Each plan in the result is a JSON object with the following attributes:
  * - *nodes*: the array of execution nodes of the plan. The array of available node types
  *   can be found [here](../../AQL/ExecutionAndPerformance/Optimizer.html)
  * 
  * - *estimatedCost*: the total estimated cost for the plan. If there are multiple
  *   plans, the optimizer will choose the plan with the lowest total cost.
  * 
  * - *collections*: an array of collections used in the query
  * 
  * - *rules*: an array of rules the optimizer applied. An overview of the
  *   available rules can be found [here](../../AQL/ExecutionAndPerformance/Optimizer.html)
  * 
  * - *variables*: array of variables used in the query (note: this may contain
  *   internal variables created by the optimizer)
  * 
  * 
  * 
  * 
  * **Example:**
  *  Valid query
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products RETURN p"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"plan"</span> : { 
  * </code><code>    <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"EnumerateCollectionNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">1</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">12</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>        <span class="hljs-string">"random"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"projections"</span> : [ ], 
  * </code><code>        <span class="hljs-string">"producesResult"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>        <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>        <span class="hljs-string">"satellite"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">2</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">22</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"rules"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"collections"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"read"</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"variables"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">22</span>, 
  * </code><code>    <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>    <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"cacheable"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"warnings"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"rulesExecuted"</span> : <span class="hljs-number">35</span>, 
  * </code><code>    <span class="hljs-string">"rulesSkipped"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"plansCreated"</span> : <span class="hljs-number">1</span> 
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
  *  A plan with some optimizer rules applied
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products LET a = p.id FILTER a == 4 LET name = p.name SORT p.id LIMIT 1 RETURN name"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"plan"</span> : { 
  * </code><code>    <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"IndexNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">1</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">11</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">4.321928094887362</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"projections"</span> : [ ], 
  * </code><code>        <span class="hljs-string">"producesResult"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>        <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>        <span class="hljs-string">"satellite"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"needsGatherNodeSort"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"indexCoversProjections"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>          { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-string">"104254"</span>, 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"skiplist"</span>, 
  * </code><code>            <span class="hljs-string">"fields"</span> : [ 
  * </code><code>              <span class="hljs-string">"id"</span> 
  * </code><code>            ], 
  * </code><code>            <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>            <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>            <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>          } 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"condition"</span> : { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"n-ary or"</span>, 
  * </code><code>          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">63</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"n-ary and"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">62</span>, 
  * </code><code>              <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"compare =="</span>, 
  * </code><code>                  <span class="hljs-string">"typeID"</span> : <span class="hljs-number">25</span>, 
  * </code><code>                  <span class="hljs-string">"excludesNull"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>                  <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                    { 
  * </code><code>                      <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>                      <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>                      <span class="hljs-string">"name"</span> : <span class="hljs-string">"id"</span>, 
  * </code><code>                      <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                        { 
  * </code><code>                          <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                          <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>                          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>                        } 
  * </code><code>                      ] 
  * </code><code>                    }, 
  * </code><code>                    { 
  * </code><code>                      <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                      <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>                      <span class="hljs-string">"value"</span> : <span class="hljs-number">4</span>, 
  * </code><code>                      <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>                      <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>                    } 
  * </code><code>                  ] 
  * </code><code>                } 
  * </code><code>              ] 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"sorted"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"ascending"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"reverse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"evalFCalls"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>        <span class="hljs-string">"fullRange"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"limit"</span> : <span class="hljs-number">0</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">11</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">5.321928094887362</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"expression"</span> : { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"compare =="</span>, 
  * </code><code>          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">25</span>, 
  * </code><code>          <span class="hljs-string">"excludesNull"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"id"</span>, 
  * </code><code>              <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                { 
  * </code><code>                  <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                  <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                  <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>                  <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>                } 
  * </code><code>              ] 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">4</span>, 
  * </code><code>              <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>              <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"simple"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"FilterNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">4</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">5</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">6.321928094887362</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>        } 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"LimitNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">5</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">9</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">7.321928094887362</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"offset"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"limit"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"fullCount"</span> : <span class="hljs-literal">false</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">9</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">8.321928094887362</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"expression"</span> : { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>              <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>              <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"attribute"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">6</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">10</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">9.321928094887362</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"rules"</span> : [ 
  * </code><code>      <span class="hljs-string">"move-calculations-up"</span>, 
  * </code><code>      <span class="hljs-string">"remove-redundant-calculations"</span>, 
  * </code><code>      <span class="hljs-string">"remove-unnecessary-calculations"</span>, 
  * </code><code>      <span class="hljs-string">"move-calculations-up-2"</span>, 
  * </code><code>      <span class="hljs-string">"use-indexes"</span>, 
  * </code><code>      <span class="hljs-string">"use-index-for-sort"</span>, 
  * </code><code>      <span class="hljs-string">"remove-unnecessary-calculations-2"</span>, 
  * </code><code>      <span class="hljs-string">"move-calculations-down"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"collections"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"read"</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"variables"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"5"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"a"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">9.321928094887362</span>, 
  * </code><code>    <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>    <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"cacheable"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"warnings"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"rulesExecuted"</span> : <span class="hljs-number">35</span>, 
  * </code><code>    <span class="hljs-string">"rulesSkipped"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"plansCreated"</span> : <span class="hljs-number">1</span> 
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
  *  Using some options
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products LET a = p.id FILTER a == 4 LET name = p.name SORT p.id LIMIT 1 RETURN name"</span>, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"maxNumberOfPlans"</span> : <span class="hljs-number">2</span>, 
  * </code><code>    <span class="hljs-string">"allPlans"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"optimizer"</span> : { 
  * </code><code>      <span class="hljs-string">"rules"</span> : [ 
  * </code><code>        <span class="hljs-string">"-all"</span>, 
  * </code><code>        <span class="hljs-string">"+use-index-for-sort"</span>, 
  * </code><code>        <span class="hljs-string">"+use-index-range"</span> 
  * </code><code>      ] 
  * </code><code>    } 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"plans"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"IndexNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">1</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">11</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">11</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"projections"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"producesResult"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>          <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>          <span class="hljs-string">"satellite"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"needsGatherNodeSort"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"indexCoversProjections"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"id"</span> : <span class="hljs-string">"104300"</span>, 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"skiplist"</span>, 
  * </code><code>              <span class="hljs-string">"fields"</span> : [ 
  * </code><code>                <span class="hljs-string">"id"</span> 
  * </code><code>              ], 
  * </code><code>              <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>              <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>              <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"condition"</span> : { 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"sorted"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"ascending"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"reverse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"evalFCalls"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"fullRange"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"limit"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">11</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">21</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"id"</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>                <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"a"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"attribute"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">3</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">31</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"compare =="</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">25</span>, 
  * </code><code>            <span class="hljs-string">"excludesNull"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                <span class="hljs-string">"name"</span> : <span class="hljs-string">"a"</span>, 
  * </code><code>                <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span> 
  * </code><code>              }, 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>                <span class="hljs-string">"value"</span> : <span class="hljs-number">4</span>, 
  * </code><code>                <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>                <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"simple"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"FilterNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">4</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">5</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">41</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>          } 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">5</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">51</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>                <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"attribute"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">6</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">7</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">61</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"expression"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"id"</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>                <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"5"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"attribute"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"LimitNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">7</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">9</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">62</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"offset"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"limit"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"fullCount"</span> : <span class="hljs-literal">false</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">9</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">10</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">63</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"rules"</span> : [ 
  * </code><code>        <span class="hljs-string">"use-index-for-sort"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"collections"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"read"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"variables"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"5"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"name"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"a"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">63</span>, 
  * </code><code>      <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"warnings"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"rulesExecuted"</span> : <span class="hljs-number">3</span>, 
  * </code><code>    <span class="hljs-string">"rulesSkipped"</span> : <span class="hljs-number">32</span>, 
  * </code><code>    <span class="hljs-string">"plansCreated"</span> : <span class="hljs-number">1</span> 
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
  *  Returning all plans
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products FILTER p.id == 25 RETURN p"</span>, 
  * </code><code>  <span class="hljs-string">"options"</span> : { 
  * </code><code>    <span class="hljs-string">"allPlans"</span> : <span class="hljs-literal">true</span> 
  * </code><code>  } 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"plans"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"IndexNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">1</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1.99</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"projections"</span> : [ ], 
  * </code><code>          <span class="hljs-string">"producesResult"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"database"</span> : <span class="hljs-string">"_system"</span>, 
  * </code><code>          <span class="hljs-string">"collection"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>          <span class="hljs-string">"satellite"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"needsGatherNodeSort"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"indexCoversProjections"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"indexes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"id"</span> : <span class="hljs-string">"104216"</span>, 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"hash"</span>, 
  * </code><code>              <span class="hljs-string">"fields"</span> : [ 
  * </code><code>                <span class="hljs-string">"id"</span> 
  * </code><code>              ], 
  * </code><code>              <span class="hljs-string">"selectivityEstimate"</span> : <span class="hljs-number">1</span>, 
  * </code><code>              <span class="hljs-string">"unique"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>              <span class="hljs-string">"sparse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>              <span class="hljs-string">"deduplicate"</span> : <span class="hljs-literal">true</span> 
  * </code><code>            } 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"condition"</span> : { 
  * </code><code>            <span class="hljs-string">"type"</span> : <span class="hljs-string">"n-ary or"</span>, 
  * </code><code>            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">63</span>, 
  * </code><code>            <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>              { 
  * </code><code>                <span class="hljs-string">"type"</span> : <span class="hljs-string">"n-ary and"</span>, 
  * </code><code>                <span class="hljs-string">"typeID"</span> : <span class="hljs-number">62</span>, 
  * </code><code>                <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                  { 
  * </code><code>                    <span class="hljs-string">"type"</span> : <span class="hljs-string">"compare =="</span>, 
  * </code><code>                    <span class="hljs-string">"typeID"</span> : <span class="hljs-number">25</span>, 
  * </code><code>                    <span class="hljs-string">"excludesNull"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>                    <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                      { 
  * </code><code>                        <span class="hljs-string">"type"</span> : <span class="hljs-string">"attribute access"</span>, 
  * </code><code>                        <span class="hljs-string">"typeID"</span> : <span class="hljs-number">35</span>, 
  * </code><code>                        <span class="hljs-string">"name"</span> : <span class="hljs-string">"id"</span>, 
  * </code><code>                        <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>                          { 
  * </code><code>                            <span class="hljs-string">"type"</span> : <span class="hljs-string">"reference"</span>, 
  * </code><code>                            <span class="hljs-string">"typeID"</span> : <span class="hljs-number">45</span>, 
  * </code><code>                            <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span>, 
  * </code><code>                            <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span> 
  * </code><code>                          } 
  * </code><code>                        ] 
  * </code><code>                      }, 
  * </code><code>                      { 
  * </code><code>                        <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>                        <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>                        <span class="hljs-string">"value"</span> : <span class="hljs-number">25</span>, 
  * </code><code>                        <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>                        <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>                      } 
  * </code><code>                    ] 
  * </code><code>                  } 
  * </code><code>                ] 
  * </code><code>              } 
  * </code><code>            ] 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"sorted"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"ascending"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"reverse"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"evalFCalls"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>          <span class="hljs-string">"fullRange"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>          <span class="hljs-string">"limit"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>          <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>            <span class="hljs-number">6</span> 
  * </code><code>          ], 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">5</span>, 
  * </code><code>          <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">2.99</span>, 
  * </code><code>          <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>          <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>            <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>            <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>          }, 
  * </code><code>          <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"rules"</span> : [ 
  * </code><code>        <span class="hljs-string">"use-indexes"</span>, 
  * </code><code>        <span class="hljs-string">"remove-filter-covered-by-index"</span>, 
  * </code><code>        <span class="hljs-string">"remove-unnecessary-calculations-2"</span> 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"collections"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"products"</span>, 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"read"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"variables"</span> : [ 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>        }, 
  * </code><code>        { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"p"</span> 
  * </code><code>        } 
  * </code><code>      ], 
  * </code><code>      <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">2.99</span>, 
  * </code><code>      <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>      <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>      <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"warnings"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"rulesExecuted"</span> : <span class="hljs-number">35</span>, 
  * </code><code>    <span class="hljs-string">"rulesSkipped"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"plansCreated"</span> : <span class="hljs-number">1</span> 
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
  *  A query that produces a warning
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR i IN 1..10 RETURN 1 / 0"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"plan"</span> : { 
  * </code><code>    <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">1</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"expression"</span> : { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"range"</span>, 
  * </code><code>          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">49</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">1</span>, 
  * </code><code>              <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>              <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">10</span>, 
  * </code><code>              <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>              <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"simple"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">2</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"expression"</span> : { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>          <span class="hljs-string">"value"</span> : <span class="hljs-literal">null</span>, 
  * </code><code>          <span class="hljs-string">"vType"</span> : <span class="hljs-string">"null"</span>, 
  * </code><code>          <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">0</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"json"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"EnumerateListNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">4</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">13</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span> 
  * </code><code>        } 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">3</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">5</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">23</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"rules"</span> : [ 
  * </code><code>      <span class="hljs-string">"move-calculations-up"</span>, 
  * </code><code>      <span class="hljs-string">"move-calculations-up-2"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"collections"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"variables"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">23</span>, 
  * </code><code>    <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">10</span>, 
  * </code><code>    <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"cacheable"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"warnings"</span> : [ 
  * </code><code>    { 
  * </code><code>      <span class="hljs-string">"code"</span> : <span class="hljs-number">1562</span>, 
  * </code><code>      <span class="hljs-string">"message"</span> : <span class="hljs-string">"division by zero"</span> 
  * </code><code>    } 
  * </code><code>  ], 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"rulesExecuted"</span> : <span class="hljs-number">35</span>, 
  * </code><code>    <span class="hljs-string">"rulesSkipped"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"plansCreated"</span> : <span class="hljs-number">1</span> 
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
  *  Invalid query (missing bind parameter)
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"query"</span> : <span class="hljs-string">"FOR p IN products FILTER p.id == @id LIMIT 2 RETURN p.n"</span> 
  * </code><code>}
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> Bad Request
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"errorMessage"</span> : <span class="hljs-string">"no value specified for declared bind parameter 'id' (while parsing)"</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">400</span>, 
  * </code><code>  <span class="hljs-string">"errorNum"</span> : <span class="hljs-number">1551</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  The data returned in the **plan** attribute of the result contains one element per AQL top-level statement
  * (i.e. `FOR`, `RETURN`, `FILTER` etc.). If the query optimizer removed some unnecessary statements,
  * the result might also contain less elements than there were top-level statements in the AQL query.
  * 
  * The following example shows a query with a non-sensible filter condition that
  * the optimizer has removed so that there are less top-level statements.
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl -X POST --header <span class="hljs-string">'accept: application/json'</span> --data-binary @- --dump - http://localhost:8529/_api/explain</span> &lt;&lt;EOF
  * </code><code>{ "query" : "FOR i IN [ 1, 2, 3 ] FILTER 1 == 2 RETURN i" }
  * </code><code>EOF
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"plan"</span> : { 
  * </code><code>    <span class="hljs-string">"nodes"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"SingletonNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"CalculationNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">1</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">1</span>, 
  * </code><code>        <span class="hljs-string">"expression"</span> : { 
  * </code><code>          <span class="hljs-string">"type"</span> : <span class="hljs-string">"array"</span>, 
  * </code><code>          <span class="hljs-string">"typeID"</span> : <span class="hljs-number">41</span>, 
  * </code><code>          <span class="hljs-string">"subNodes"</span> : [ 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">1</span>, 
  * </code><code>              <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>              <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">2</span>, 
  * </code><code>              <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>              <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>            }, 
  * </code><code>            { 
  * </code><code>              <span class="hljs-string">"type"</span> : <span class="hljs-string">"value"</span>, 
  * </code><code>              <span class="hljs-string">"typeID"</span> : <span class="hljs-number">40</span>, 
  * </code><code>              <span class="hljs-string">"value"</span> : <span class="hljs-number">3</span>, 
  * </code><code>              <span class="hljs-string">"vType"</span> : <span class="hljs-string">"int"</span>, 
  * </code><code>              <span class="hljs-string">"vTypeID"</span> : <span class="hljs-number">2</span> 
  * </code><code>            } 
  * </code><code>          ] 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"canThrow"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>        <span class="hljs-string">"expressionType"</span> : <span class="hljs-string">"json"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"NoResultsNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">2</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">7</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">0.5</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">0</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"EnumerateListNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">7</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">3</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">0.5</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"outVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span> 
  * </code><code>        } 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"type"</span> : <span class="hljs-string">"ReturnNode"</span>, 
  * </code><code>        <span class="hljs-string">"dependencies"</span> : [ 
  * </code><code>          <span class="hljs-number">3</span> 
  * </code><code>        ], 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">6</span>, 
  * </code><code>        <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">0.5</span>, 
  * </code><code>        <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"inVariable"</span> : { 
  * </code><code>          <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>          <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span> 
  * </code><code>        }, 
  * </code><code>        <span class="hljs-string">"count"</span> : <span class="hljs-literal">true</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"rules"</span> : [ 
  * </code><code>      <span class="hljs-string">"move-calculations-up"</span>, 
  * </code><code>      <span class="hljs-string">"move-filters-up"</span>, 
  * </code><code>      <span class="hljs-string">"remove-unnecessary-filters"</span>, 
  * </code><code>      <span class="hljs-string">"remove-unnecessary-calculations"</span> 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"collections"</span> : [ ], 
  * </code><code>    <span class="hljs-string">"variables"</span> : [ 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">4</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"3"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">2</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"1"</span> 
  * </code><code>      }, 
  * </code><code>      { 
  * </code><code>        <span class="hljs-string">"id"</span> : <span class="hljs-number">0</span>, 
  * </code><code>        <span class="hljs-string">"name"</span> : <span class="hljs-string">"i"</span> 
  * </code><code>      } 
  * </code><code>    ], 
  * </code><code>    <span class="hljs-string">"estimatedCost"</span> : <span class="hljs-number">0.5</span>, 
  * </code><code>    <span class="hljs-string">"estimatedNrItems"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"initialize"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>    <span class="hljs-string">"isModificationQuery"</span> : <span class="hljs-literal">false</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"cacheable"</span> : <span class="hljs-literal">true</span>, 
  * </code><code>  <span class="hljs-string">"warnings"</span> : [ ], 
  * </code><code>  <span class="hljs-string">"stats"</span> : { 
  * </code><code>    <span class="hljs-string">"rulesExecuted"</span> : <span class="hljs-number">35</span>, 
  * </code><code>    <span class="hljs-string">"rulesSkipped"</span> : <span class="hljs-number">0</span>, 
  * </code><code>    <span class="hljs-string">"plansCreated"</span> : <span class="hljs-number">1</span> 
  * </code><code>  }, 
  * </code><code>  <span class="hljs-string">"error"</span> : <span class="hljs-literal">false</span>, 
  * </code><code>  <span class="hljs-string">"code"</span> : <span class="hljs-number">200</span> 
  * </code><code>}
  * </code></pre>
  */
  def post(body: PostAPIExplain): Future[ArangoResponse] = client
    .method(HttpMethod.Post)
    .path(path"/_db/_system/_api/explain".withArguments(Map()))
    .restful[PostAPIExplain, ArangoResponse](body)
}