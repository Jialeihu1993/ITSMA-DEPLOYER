#!/bin/bash
mkdir -p /pv/itsma/services /pv/itsma/deployer/conf

export time_zone=$MASTERNODE_TIME_ZONE
source /bin/sync_time_zone.sh
sync_time_zone

pushd /app/itsma-deployer
java -Xbootclasspath/a:/yamls_output/ojdbc.jar: -jar deployer-backend.jar --server.port=8081 &
popd

pushd /app/deployer-ui
node ./app/server.js
popd