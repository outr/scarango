#!/usr/bin/env bash

set -e

sbt +clean
sbt test
sbt +api/publishSigned +coreJS/publishSigned +coreJVM/publishSigned +driver/publishSigned +monitored/publishSigned
sbt plugin/publishSigned
sbt sonatypeBundleRelease
