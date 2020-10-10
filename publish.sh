#!/usr/bin/env bash

set -e

sbt +clean
sbt test
sbt +api/publishSigned +coreJS/publishSigned +coreJVM/publishSigned +driver/publishSigned +monitored/publishSigned ++2.12.8 plugin/publishSigned
sbt sonatypeBundleRelease
