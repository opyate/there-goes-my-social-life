#!/bin/bash
scp scripts/solr root@bopango.net:/etc/init.d/
ssh root@bopango.net "chmod +x /etc/init.d/solr"
ssh root@bopango.net "chkconfig --add solr"
exit 0

# The below is not necessary if we run chkconfig
ssh root@bopango.net "rm /etc/rc3.d/S99solr"
ssh root@bopango.net "ln -s /etc/init.d/solr /etc/rc3.d/S99solr"
ssh root@bopango.net "rm /etc/rc3.d/K01solr"
ssh root@bopango.net "ln -s /etc/init.d/solr /etc/rc3.d/K01solr"

