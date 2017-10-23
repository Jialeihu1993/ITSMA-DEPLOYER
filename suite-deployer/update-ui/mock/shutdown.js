'use strict';
let random = require('lodash/random');
let floor = require('lodash/floor');
// just for simulation
// available values: DEPLOYED, WAIT, READY, FAILED, SUCCESS, RUNNING, TERMINATING, TERMINATED
const STATUS = [
                'TERMINATING', //shutdown in process
                'TERMINATED',  //shutdown finish
                'RUNNING'
            ];
//shutdown phase

const PHASE = [
                'running',
                'shuttingdown'
            ];

let shutdownStatus = {
                        "phase": {
                            "name": "running", // available values: idle, running, installing, shuttingdown, updating, restoring
                            "detail": "" // available values: installing.vault, updating.statistic, updatring.vault, updating.install
                        },
                        "itsmaServiceStatuses":[
                                {"name":"itom-auth","status":"RUNNING"},  // available values: DEPLOYED, WAIT, READY, FAILED, SUCCESS, RUNNING, TERMINATING, TERMINATED
                                {"name":"itom-idm","status":"RUNNING"},
                                {"name":"itom-openldap","status":"RUNNING"}
                        ]
                     };


module.exports = function (app) {
  let count = 0
  let serviceIndex = 0;
  let length = shutdownStatus.itsmaServiceStatuses.length;
  //get
  app.use('/api/upgrade/shutdown/status', function (req, res) {
    if(count == 0){
        shutdownStatus.phase.name = 'running';
        count ++;
    }else{
        if(serviceIndex >= length){
            shutdownStatus.phase.name = 'shuttingdown';
            shutdownStatus.itsmaServiceStatuses[length-1].status = 'TERMINATED';
        }else{
            shutdownStatus.phase.name = 'shuttingdown';
            shutdownStatus.itsmaServiceStatuses[serviceIndex].status = 'TERMINATING';
            if(serviceIndex > 0){
                shutdownStatus.itsmaServiceStatuses[serviceIndex-1].status = 'TERMINATED';
            }
        }
        serviceIndex++;
    }
    res.json(shutdownStatus);
  });

  app.use('/api/upgrade/shutdown', function (req, res) {
    res.status(202).end();
  });


};
