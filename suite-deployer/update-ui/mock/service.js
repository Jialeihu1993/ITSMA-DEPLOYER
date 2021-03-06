'use strict';
let random = require('lodash/random');
let floor = require('lodash/floor');

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
    'shuttingdown', //this should be the initial state of upgrade
    'updating' // turn to updating and never change
];
//
let updateStatus = {
            "phase": {
                "name": "shuttingdown", // available values: idle, running, installing, shuttingdown, updating, restoring
                "detail": "" // available values: installing.vault, updating.statistic, updatring.vault, updating.install
            },  //service status change TERMINATED->READY->SUCCESS
            "itsmaServiceStatuses":[
                    {"name":"itom-auth","status":"TERMINATED"},  // available values: DEPLOYED, WAIT, READY, FAILED, SUCCESS, RUNNING, TERMINATING, TERMINATED
                    {"name":"itom-idm","status":"TERMINATED"},
                    {"name":"itom-openldap","status":"TERMINATED"}
            ]
         };


module.exports = function (app) {
let count = 0
let serviceIndex = 0;
let length = updateStatus.itsmaServiceStatuses.length;
//get
app.use('/api/upgrade/update/status', function (req, res) {
    if(count == 0){
        updateStatus.phase.name = 'shuttingdown';
        count ++;
    }else{
        if(serviceIndex >= length){
            updateStatus.phase.name = 'updating';
            updateStatus.itsmaServiceStatuses[length-1].status = 'SUCCESS';
        }else{
            updateStatus.phase.name = 'updating';
            updateStatus.itsmaServiceStatuses[serviceIndex].status = 'READY';
        if(serviceIndex > 0){
            updateStatus.itsmaServiceStatuses[serviceIndex-1].status = 'SUCCESS';
        }
    }
    serviceIndex++;
 }
res.json(updateStatus);
});

app.use('/api/upgrade/update', function (req, res) {
res.status(202).end();
});


};
