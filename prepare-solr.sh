#!/bin/bash
echo "Setting up SYSV service"
scp scripts/solr root@bopango.net:/etc/init.d/
ssh root@bopango.net "chmod +x /etc/init.d/solr"
ssh root@bopango.net "chkconfig --add solr"

echo "Setting up SOLR local properties"
ssh root@bopango.net "mkdir -p /etc/solr-bopango"
scp solr-config/bopango.net.properties root@bopango.net:/etc/solr-bopango/props.properties
exit 0

# The below is not necessary if we run chkconfig
ssh root@bopango.net "rm /etc/rc3.d/S99solr"
ssh root@bopango.net "ln -s /etc/init.d/solr /etc/rc3.d/S99solr"
ssh root@bopango.net "rm /etc/rc3.d/K01solr"
ssh root@bopango.net "ln -s /etc/init.d/solr /etc/rc3.d/K01solr"

