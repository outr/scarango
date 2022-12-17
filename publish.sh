#!/usr/bin/env bash

set -e

sbt +clean
sbt +test
sbt docs/mdoc
sbt +coreJS/publishSigned +coreJVM/publishSigned +driver/publishSigned
sbt sonatypeBundleRelease