'use strict';
let random = require('lodash/random');
let floor = require('lodash/floor');

function sleep(sleepTime) {
    for(let start = +new Date; +new Date - start <= sleepTime; );
}

// just for simulation
// available values: DEPLOYED, WAIT, READY, FAILED, SUCCESS, RUNNING, TERMINATING, TERMINATED
const STATUS = [
    'TERMINATED',  //service initial status
    'READY',    //updating state
    'SUCCESS'   //final state
];
//shutdown phase

const DETAIL = [
    'updating.statistic',
    'updatring.vault',
    'updating.install'
];

const PHASE = [
    'idle', //this should be the initial state of upgrade
    'restoring' // turn to updating and never change
];
//
let updateStatus = {
            "phase": {
                "name": "idle", // available values: idle, running, installing, shuttingdown, updating, restoring
                "detail": "" // available values: installing.vault, updating.statistic, updatring.vault, updating.install
            },  //service status change TERMINATED->READY->SUCCESS
            "itsmaServiceStatuses":[
                    {"name":"itom-auth","status":"TERMINATED"},  // available values: DEPLOYED, WAIT, READY, FAILED, SUCCESS, RUNNING, TERMINATING, TERMINATED
                    {"name":"itom-idm","status":"TERMINATED"},
                    {"name":"itom-openldap","status":"TERMINATED"},
                    {"name":"itom-idm","status":"TERMINATED"},
                    {"name":"itom-openldap","status":"TERMINATED"},
                    {"name":"itom-idm","status":"TERMINATED"},
                    {"name":"itom-openldap","status":"TERMINATED"},
                    {"name":"itom-idm","status":"TERMINATED"},
                    {"name":"itom-openldap","status":"TERMINATED"}
            ]
         };


module.exports = function (app) {
let count = 0
let serviceIndex = 0;
let length = updateStatus.itsmaServiceStatuses.length;
//get
app.use('/api/upgrade/restore/status', function (req, res) {
    if(count == 0){
        updateStatus.phase.name = 'idle';
        count ++;
    }else{
        if(serviceIndex >= length){
            updateStatus.phase.name = 'restoring';
            updateStatus.itsmaServiceStatuses[length-1].status = 'SUCCESS';
        }else{
            updateStatus.phase.name = 'restoring';
            updateStatus.itsmaServiceStatuses[serviceIndex].status = 'READY';
            if(serviceIndex == 0){
                updateStatus.phase.detail = 'updating.statistic';
            }
            if(serviceIndex == 1){
                updateStatus.phase.detail = 'updatring.vault';
            }
            if(serviceIndex == 2){
                updateStatus.phase.detail = 'updating.install';
            }
        if(serviceIndex > 0){
            updateStatus.itsmaServiceStatuses[serviceIndex-1].status = 'SUCCESS';
        }
    }
    serviceIndex++;
 }
res.json(updateStatus);
});

app.use('/api/upgrade/restore', function (req, res) {
    //sleep(10000);
    res.status(201).end();
});


};

