#!/usr/bin/env bash

set -e

sbt +clean
sbt +test
sbt +coreJS/publishSigned +coreJVM/publishSigned +driver/publishSigned
sbt sonatypeBundleRelease