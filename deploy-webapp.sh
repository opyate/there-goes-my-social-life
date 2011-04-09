#!/bin/bash
# This calls the Capistrano script to unpack and deploy the WAR file server-side.

echo "Deploying latest build to bopango.net"

sbt clean release

VERSION=$(echo "$(ls -lat target/scala_2.8.1/ | grep war | head -n1)" | sed 's/.*\([0-9]\.[0-9]\.[0-9]\).war/\1/')

echo "Version = ${VERSION}"

scp target/scala_2.8.1/bopango-website_2.8.1-${VERSION}.war root@bopango.net:~/bopango-deployments/bopango-v${VERSION}.war && cap deploywebappjetty -s version=${VERSION}

echo "Done."
