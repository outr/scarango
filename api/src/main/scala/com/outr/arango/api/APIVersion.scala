package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
object APIVersion {
  /**
  * Returns the server name and version number. The response is a JSON object
  * with the following attributes:
  * 
  * 
  * **HTTP 200**
  * *A json document with these Properties is returned:*
  * 
  * is returned in all cases.
  * 
  * - **version**: the server version string. The string has the format
  * "*major*.*minor*.*sub*". *major* and *minor* will be numeric, and *sub*
  * may contain a number or a textual version.
  * - **details**:
  *   - **failure-tests**: *false* for production binaries (the facility to invoke fatal errors is disabled)
  *   - **boost-version**: which boost version do we bind
  *   - **zlib-version**: the version of the bundled zlib
  *   - **sse42**: do we have a SSE 4.2 enabled cpu?
  *   - **assertions**: do we have assertions compiled in (=> developer version)
  *   - **jemalloc**: *true* if we use jemalloc
  *   - **arm**: *false* - this is not running on an ARM cpu
  *   - **rocksdb-version**: the rocksdb version this release bundles
  *   - **cplusplus**: C++ standards version
  *   - **sizeof int**: number of bytes for *integers*
  *   - **openssl-version**: which openssl version do we link?
  *   - **platform**: the host os - *linux*, *windows* or *darwin*
  *   - **endianness**: currently only *little* is supported
  *   - **vpack-version**: the version of the used velocypack implementation
  *   - **icu-version**: Which version of ICU do we bundle
  *   - **mode**: the mode we're runnig as - one of [*server*, *console*, *script*]
  *   - **unaligned-access**: does this system support unaligned memory access?
  *   - **build-repository**: reference to the git-ID this was compiled from
  *   - **asm-crc32**: do we have assembler implemented CRC functions?
  *   - **host**: the host ID
  *   - **fd-setsize**: if not *poll* the fd setsize is valid for the maximum number of filedescriptors
  *   - **maintainer-mode**: *false* if this is a production binary
  *   - **sizeof void***: number of bytes for *void pointers*
  *   - **asan**: has this been compiled with the asan address sanitizer turned on? (should be false)
  *   - **build-date**: the date when this binary was created
  *   - **compiler**: which compiler did we use
  *   - **server-version**: the ArangoDB release version
  *   - **fd-client-event-handler**: which method do we use to handle fd-sets, *poll* should be here on linux.
  *   - **reactor-type**: *epoll* TODO 
  *   - **architecture**: The CPU architecture, i.e. *64bit*
  *   - **debug**: *false* for production binaries
  *   - **full-version-string**: The full version string
  *   - **v8-version**: the bundled V8 javascript engine version
  * - **server**: will always contain *arango*
  * 
  * 
  * 
  * 
  * **Example:**
  *  Return the version information
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/version</span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"server"</span> : <span class="hljs-string">"arango"</span>, 
  * </code><code>  <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>  <span class="hljs-string">"license"</span> : <span class="hljs-string">"enterprise"</span> 
  * </code><code>}
  * </code></pre>
  * 
  * 
  * 
  * 
  * **Example:**
  *  Return the version information with details
  * 
  * <pre><code><span class="hljs-meta">shell&gt;</span><span class="bash"> curl --header <span class="hljs-string">'accept: application/json'</span> --dump - http://localhost:8529/_api/version?details=<span class="hljs-literal">true</span></span>
  * </code><code>
  * </code><code>HTTP/<span class="hljs-number">1.1</span> OK
  * </code><code>content-type: application/json; charset=utf<span class="hljs-number">-8</span>
  * </code><code>x-content-type-options: nosniff
  * </code><code>
  * </code><code>{ 
  * </code><code>  <span class="hljs-string">"server"</span> : <span class="hljs-string">"arango"</span>, 
  * </code><code>  <span class="hljs-string">"version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>  <span class="hljs-string">"license"</span> : <span class="hljs-string">"enterprise"</span>, 
  * </code><code>  <span class="hljs-string">"details"</span> : { 
  * </code><code>    <span class="hljs-string">"architecture"</span> : <span class="hljs-string">"64bit"</span>, 
  * </code><code>    <span class="hljs-string">"arm"</span> : <span class="hljs-string">"false"</span>, 
  * </code><code>    <span class="hljs-string">"asan"</span> : <span class="hljs-string">"false"</span>, 
  * </code><code>    <span class="hljs-string">"asm-crc32"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"assertions"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"avx2"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"boost-version"</span> : <span class="hljs-string">"1.69.0"</span>, 
  * </code><code>    <span class="hljs-string">"build-date"</span> : <span class="hljs-string">"2019-02-20 08:48:55"</span>, 
  * </code><code>    <span class="hljs-string">"build-repository"</span> : <span class="hljs-string">"heads/bug-fix/fix-query-cache-shutdown-0-gd977f1786a"</span>, 
  * </code><code>    <span class="hljs-string">"compiler"</span> : <span class="hljs-string">"gcc [8.2.0]"</span>, 
  * </code><code>    <span class="hljs-string">"cplusplus"</span> : <span class="hljs-string">"201402"</span>, 
  * </code><code>    <span class="hljs-string">"curl-version"</span> : <span class="hljs-string">"libcurl/7.63.0 OpenSSL/1.1.0g"</span>, 
  * </code><code>    <span class="hljs-string">"debug"</span> : <span class="hljs-string">"false"</span>, 
  * </code><code>    <span class="hljs-string">"endianness"</span> : <span class="hljs-string">"little"</span>, 
  * </code><code>    <span class="hljs-string">"enterprise-version"</span> : <span class="hljs-string">"enterprise"</span>, 
  * </code><code>    <span class="hljs-string">"failure-tests"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"fd-client-event-handler"</span> : <span class="hljs-string">"poll"</span>, 
  * </code><code>    <span class="hljs-string">"fd-setsize"</span> : <span class="hljs-string">"1024"</span>, 
  * </code><code>    <span class="hljs-string">"full-version-string"</span> : <span class="hljs-string">"ArangoDB 3.5.0-devel enterprise [linux] 64bit maintainer mode, using jemalloc, build heads/bug-fix/fix-query-cache-shutdown-0-gd977f1786a, VPack 0.1.33, RocksDB 5.18.0, ICU 58.1, V8 7.1.302.28, OpenSSL 1.1.0g  2 Nov 2017"</span>, 
  * </code><code>    <span class="hljs-string">"icu-version"</span> : <span class="hljs-string">"58.1"</span>, 
  * </code><code>    <span class="hljs-string">"iresearch-version"</span> : <span class="hljs-string">"1.0.0.0"</span>, 
  * </code><code>    <span class="hljs-string">"jemalloc"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"license"</span> : <span class="hljs-string">"enterprise"</span>, 
  * </code><code>    <span class="hljs-string">"maintainer-mode"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"ndebug"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"openssl-version-compile-time"</span> : <span class="hljs-string">"OpenSSL 1.1.0g  2 Nov 2017"</span>, 
  * </code><code>    <span class="hljs-string">"openssl-version-run-time"</span> : <span class="hljs-string">"OpenSSL 1.1.0g  2 Nov 2017"</span>, 
  * </code><code>    <span class="hljs-string">"optimization-flags"</span> : <span class="hljs-string">"-march=haswell -msse2 -msse3 -mssse3 -msse4.1 -msse4.2 -mavx -mfma -mbmi2 -mavx2 -mno-sse4a -mno-xop -mno-fma4 -mno-avx512f -mno-avx512vl -mno-avx512pf -mno-avx512er -mno-avx512cd -mno-avx512dq -mno-avx512bw -mno-avx512ifma -mno-avx512vbmi"</span>, 
  * </code><code>    <span class="hljs-string">"platform"</span> : <span class="hljs-string">"linux"</span>, 
  * </code><code>    <span class="hljs-string">"reactor-type"</span> : <span class="hljs-string">"epoll"</span>, 
  * </code><code>    <span class="hljs-string">"rocksdb-version"</span> : <span class="hljs-string">"5.18.0"</span>, 
  * </code><code>    <span class="hljs-string">"server-version"</span> : <span class="hljs-string">"3.5.0-devel"</span>, 
  * </code><code>    <span class="hljs-string">"sizeof int"</span> : <span class="hljs-string">"4"</span>, 
  * </code><code>    <span class="hljs-string">"sizeof long"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>    <span class="hljs-string">"sizeof void*"</span> : <span class="hljs-string">"8"</span>, 
  * </code><code>    <span class="hljs-string">"sse42"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"unaligned-access"</span> : <span class="hljs-string">"true"</span>, 
  * </code><code>    <span class="hljs-string">"v8-version"</span> : <span class="hljs-string">"7.1.302.28"</span>, 
  * </code><code>    <span class="hljs-string">"vpack-version"</span> : <span class="hljs-string">"0.1.33"</span>, 
  * </code><code>    <span class="hljs-string">"zlib-version"</span> : <span class="hljs-string">"1.2.11"</span>, 
  * </code><code>    <span class="hljs-string">"mode"</span> : <span class="hljs-string">"server"</span>, 
  * </code><code>    <span class="hljs-string">"host"</span> : <span class="hljs-string">"c54ebb83e5eb4257b9e0f7201ba87ded"</span> 
  * </code><code>  } 
  * </code><code>}
  * </code></pre>
  */
  def get(client: HttpClient, details: Option[Boolean] = None): Future[GetAPIReturnRc200] = client
    .method(HttpMethod.Get)
    .path(path"/_api/version", append = true) 
    .param[Option[Boolean]]("details", details, None)
    .call[GetAPIReturnRc200]
}