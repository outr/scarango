#!/usr/bin/env bash

set -e

sbt +clean
sbt +compile
sbt +test
sbt +api/publishSigned +coreJS/publishSigned +coreJVM/publishSigned +driver/publishSigned ++2.12.8 plugin/publishSigned sonatypeRelease