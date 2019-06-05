package com.outr.arango.api

import com.outr.arango.api.model._
import io.youi.client.HttpClient
import io.youi.http.HttpMethod
import io.youi.net._
import io.circe.Json
import scala.concurrent.Future
import scribe.Execution.global
      
class APIFoxx(client: HttpClient) {
  /**
  * Fetches a list of services installed in the current database.
  * 
  * Returns a list of objects with the following attributes:
  * 
  * - *mount*: the mount path of the service
  * - *development*: *true* if the service is running in development mode
  * - *legacy*: *true* if the service is running in 2.8 legacy compatibility mode
  * - *provides*: the service manifest's *provides* value or an empty object
  * 
  * Additionally the object may contain the following attributes if they have been set on the manifest:
  * 
  * - *name*: a string identifying the service type
  * - *version*: a semver-compatible version string
  */
  def get(excludeSystem: Option[Boolean] = None): Future[Json] = client
    .method(HttpMethod.Get)
    .path(path"/_api/foxx", append = true) 
    .param[Option[Boolean]]("excludeSystem", excludeSystem, None)
    .call[Json]

  /**
  * Installs the given new service at the given mount path.
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
  def post(mount: String, development: Option[Boolean] = None, setup: Option[Boolean] = None, legacy: Option[Boolean] = None): Future[Json] = client
    .method(HttpMethod.Post)
    .path(path"/_api/foxx", append = true) 
    .params("mount" -> mount.toString)
    .param[Option[Boolean]]("development", development, None)
    .param[Option[Boolean]]("setup", setup, None)
    .param[Option[Boolean]]("legacy", legacy, None)
    .call[Json]
}