package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxxService(client: HttpClient) {
  /**
  * Removes the service at the given mount path from the database and file system.
  * 
  * Returns an empty response on success.
  */
  def delete(mount: String, teardown: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Delete)
    .path(path"/_db/_system/_api/foxx/service".withArguments(Map()))
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .call[ArangoResponse]

  /**
  * Fetches detailed information for the service at the given mount path.
  * 
  * Returns an object with the following attributes:
  * 
  * - *mount*: the mount path of the service
  * - *path*: the local file system path of the service
  * - *development*: *true* if the service is running in development mode
  * - *legacy*: *true* if the service is running in 2.8 legacy compatibility mode
  * - *manifest*: the normalized JSON manifest of the service
  * 
  * Additionally the object may contain the following attributes if they have been set on the manifest:
  * 
  * - *name*: a string identifying the service type
  * - *version*: a semver-compatible version string
  */
  def get(mount: String): Future[ArangoResponse] = client
    .method(HttpMethod.Get)
    .path(path"/_db/_system/_api/foxx/service".withArguments(Map()))
    .params("mount" -> mount.toString)
    .call[ArangoResponse]

  /**
  * Installs the given new service on top of the service currently installed at the given mount path.
  * This is only recommended for switching between different versions of the same service.
  * 
  * Unlike replacing a service, upgrading a service retains the old service's configuration
  * and dependencies (if any) and should therefore only be used to migrate an existing service
  * to a newer or equivalent service.
  * 
  * The request body can be any of the following formats:
  * 
  * - `application/zip`: a raw zip bundle containing a service
  * - `application/javascript`: a standalone JavaScript file
  * - `application/json`: a service definition as JSON
  * - `multipart/form-data`: a service definition as a multipart form
  * 
  * A service definition is an object or form with the following properties or fields:
  * 
  * - *configuration*: a JSON object describing configuration values
  * - *dependencies*: a JSON object describing dependency settings
  * - *source*: a fully qualified URL or an absolute path on the server's file system
  * 
  * When using multipart data, the *source* field can also alternatively be a file field
  * containing either a zip bundle or a standalone JavaScript file.
  * 
  * When using a standalone JavaScript file the given file will be executed
  * to define our service's HTTP endpoints. It is the same which would be defined
  * in the field `main` of the service manifest.
  * 
  * If *source* is a URL, the URL must be reachable from the server.
  * If *source* is a file system path, the path will be resolved on the server.
  * In either case the path or URL is expected to resolve to a zip bundle,
  * JavaScript file or (in case of a file system path) directory.
  * 
  * Note that when using file system paths in a cluster with multiple coordinators
  * the file system path must resolve to equivalent files on every coordinator.
  */
  def patch(mount: String, teardown: Option[Boolean] = None, setup: Option[Boolean] = None, legacy: Option[Boolean] = None, force: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Patch)
    .path(path"/_db/_system/_api/foxx/service".withArguments(Map()))
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .param[Option[Boolean]]("setup", setup, None)
    .param[Option[Boolean]]("legacy", legacy, None)
    .param[Option[Boolean]]("force", force, None)
    .call[ArangoResponse]

  /**
  * Removes the service at the given mount path from the database and file system.
  * Then installs the given new service at the same mount path.
  * 
  * This is a slightly safer equivalent to performing an uninstall of the old service
  * followed by installing the new service. The new service's main and script files
  * (if any) will be checked for basic syntax errors before the old service is removed.
  * 
  * The request body can be any of the following formats:
  * 
  * - `application/zip`: a raw zip bundle containing a service
  * - `application/javascript`: a standalone JavaScript file
  * - `application/json`: a service definition as JSON
  * - `multipart/form-data`: a service definition as a multipart form
  * 
  * A service definition is an object or form with the following properties or fields:
  * 
  * - *configuration*: a JSON object describing configuration values
  * - *dependencies*: a JSON object describing dependency settings
  * - *source*: a fully qualified URL or an absolute path on the server's file system
  * 
  * When using multipart data, the *source* field can also alternatively be a file field
  * containing either a zip bundle or a standalone JavaScript file.
  * 
  * When using a standalone JavaScript file the given file will be executed
  * to define our service's HTTP endpoints. It is the same which would be defined
  * in the field `main` of the service manifest.
  * 
  * If *source* is a URL, the URL must be reachable from the server.
  * If *source* is a file system path, the path will be resolved on the server.
  * In either case the path or URL is expected to resolve to a zip bundle,
  * JavaScript file or (in case of a file system path) directory.
  * 
  * Note that when using file system paths in a cluster with multiple coordinators
  * the file system path must resolve to equivalent files on every coordinator.
  */
  def put(mount: String, teardown: Option[Boolean] = None, setup: Option[Boolean] = None, legacy: Option[Boolean] = None, force: Option[Boolean] = None): Future[ArangoResponse] = client
    .method(HttpMethod.Put)
    .path(path"/_db/_system/_api/foxx/service".withArguments(Map()))
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("teardown", teardown, None)
    .param[Option[Boolean]]("setup", setup, None)
    .param[Option[Boolean]]("legacy", legacy, None)
    .param[Option[Boolean]]("force", force, None)
    .call[ArangoResponse]
}