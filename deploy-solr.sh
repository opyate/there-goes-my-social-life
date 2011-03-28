#!/bin/bash
# This calls the Capistrano script to unpack and deploy the SOLR HOME server-side.

echo "Deploying latest SOLR HOME to bopango.net"

echo "Creating tarball of SOLR Home"
rm solr-home.tgz
tar czf solr-home.tgz solr-home
ssh root@bopango.net "mkdir -p /root/solr-home-deploys"
scp solr-home.tgz root@bopango.net:/root/solr-home-deploys

echo "Deploying SOLR Home"
cap deploysolr
rm solr-home.tgz

echo "Done."
