'use strict';
let random = require('lodash/random');
let floor = require('lodash/floor');
let clone = require('lodash/clone');
let concat = require('lodash/concat');


const backupStatus = [
        'WAIT',
        'BACKUPING',
        'FAILED',
        'SUCCESS'
];

const backupProcess = [
        'BACKUP_VALIDATION',
        'GENERATE_METADATA',
        'BACKUP_CONFIGMAP_YAML',
        'BACKUP_SERVICE_YAML',
        'BACKUP_INGRESS_YAML',
        'BACKUP_POD_YAML',
        'BACKUP_DEPLOYMENT_YAML',
        'BACKUP_DAEMONSET_YAML',
        'BACKUP_SECRET_YAML',
        'BACKUP_PV_YAML',
        'BACKUP_PVC_YAML',
        'BACKUP_PV_VOLUMES',
        'COMPRESS_BACKUP_PACKAGE',
        'REMOVE_COMPRESSED_PACKAGE'
];

const backupErrorCode = [
        'GENERATE_METADATA_ERROR',
        'BACKUP_CONFIGMAP_YAML_ERROR',
        'BACKUP_SERVICE_YAML_ERROR',
        'BACKUP_INGRESS_YAML_ERROR',
        'BACKUP_POD_YAML_ERROR',
        'BACKUP_PV_VOLUMES_ERROR',
        'COMPRESS_BACKUP_PACKAGE_ERROR',
        '0' // no error
];
// new backup file metadata would be added quickly after backup start
let mockDataGetFiles = {
    'status': 200,
    'backup_list':[
        {
            "itom_suite_backup_package_dir": "1504770568299",
            "itom_suite_size": "xsmall",
            "itom_suite_backup_package_name": "ITSMA_v2017.07_1504770568299.zip",
            "itom_suite_version": "2017.07",
            "itom_suite_external_db": false,
            "itom_suite_mode": "H_MODE",
            "itom_suite_backup_time": "Thu Sep 07 07:49:28 UTC 2017",
            "itom_suite_external_ldap": false,
            "activated_services": [
                {
                    "name": "itom-sm"
                },
                {
                    "name": "itom-cmdb"
                },
                {
                    "name": "itom-xservices"
                },
                {
                    "name": "itom-xservices-infra"
                },
                {
                    "name": "itom-service-portal"
                },
                {
                    "name": "itom-service-portal-ui"
                },
                {
                    "name": "itom-smartanalytics"
                }
            ]
        },
        {
            "itom_suite_backup_package_dir": "1504771952538",
            "itom_suite_size": "xsmall",
            "itom_suite_backup_package_name": "ITSMA_v2017.07_1504771952538.zip",
            "itom_suite_version": "2017.07",
            "itom_suite_external_db": false,
            "itom_suite_mode": "H_MODE",
            "itom_suite_backup_time": "Thu Sep 07 08:12:32 UTC 2017",
            "itom_suite_external_ldap": false,
            "activated_services": [
                {
                    "name": "itom-sm"
                },
                {
                    "name": "itom-cmdb"
                },
                {
                    "name": "itom-xservices"
                },
                {
                    "name": "itom-xservices-infra"
                },
                {
                    "name": "itom-service-portal"
                },
                {
                    "name": "itom-service-portal-ui"
                },
                {
                    "name": "itom-smartanalytics"
                }
            ]
        },
        {
            "itom_suite_backup_package_dir": "1504771993444",
            "itom_suite_size": "xsmall",
            "itom_suite_backup_package_name": "ITSMA_v2017.07_1504771993444.zip",
            "itom_suite_version": "2017.07",
            "itom_suite_external_db": false,
            "itom_suite_mode": "H_MODE",
            "itom_suite_backup_time": "Thu Sep 07 08:13:13 UTC 2017",
            "itom_suite_external_ldap": false,
            "activated_services": [
                {
                    "name": "itom-sm"
                },
                {
                    "name": "itom-cmdb"
                },
                {
                    "name": "itom-xservices"
                },
                {
                    "name": "itom-xservices-infra"
                },
                {
                    "name": "itom-service-portal"
                },
                {
                    "name": "itom-service-portal-ui"
                },
                {
                    "name": "itom-smartanalytics"
                }
            ]
        },
        {
            "itom_suite_backup_package_dir": "1504772018365",
            "itom_suite_size": "xsmall",
            "itom_suite_backup_package_name": "ITSMA_v2017.07_1504772018365.zip",
            "itom_suite_version": "2017.07",
            "itom_suite_external_db": false,
            "itom_suite_mode": "H_MODE",
            "itom_suite_backup_time": "Thu Sep 07 08:13:38 UTC 2017",
            "itom_suite_external_ldap": false,
            "activated_services": [
                {
                    "name": "itom-sm"
                },
                {
                    "name": "itom-cmdb"
                },
                {
                    "name": "itom-xservices"
                },
                {
                    "name": "itom-xservices-infra"
                },
                {
                    "name": "itom-service-portal"
                },
                {
                    "name": "itom-service-portal-ui"
                },
                {
                    "name": "itom-smartanalytics"
                }
            ]
        },
        {
            "itom_suite_backup_package_dir": "1504772038238",
            "itom_suite_size": "xsmall",
            "itom_suite_backup_package_name": "ITSMA_v2017.07_1504772038238.zip",
            "itom_suite_version": "2017.07",
            "itom_suite_external_db": false,
            "itom_suite_mode": "H_MODE",
            "itom_suite_backup_time": "Thu Sep 07 08:13:58 UTC 2017",
            "itom_suite_external_ldap": false,
            "activated_services": [
                {
                    "name": "itom-sm"
                },
                {
                    "name": "itom-cmdb"
                },
                {
                    "name": "itom-xservices"
                },
                {
                    "name": "itom-xservices-infra"
                },
                {
                    "name": "itom-service-portal"
                },
                {
                    "name": "itom-service-portal-ui"
                },
                {
                    "name": "itom-smartanalytics"
                }
            ]
        },
        {
            "itom_suite_backup_package_dir": "1504773372264",
            "itom_suite_size": "xsmall",
            "itom_suite_backup_package_name": "ITSMA_v2017.07_1504773372264.zip",
            "itom_suite_version": "2017.07",
            "itom_suite_external_db": false,
            "itom_suite_mode": "H_MODE",
            "itom_suite_backup_time": "Thu Sep 07 08:36:12 UTC 2017",
            "itom_suite_external_ldap": false,
            "activated_services": [
                {
                    "name": "itom-sm"
                },
                {
                    "name": "itom-cmdb"
                },
                {
                    "name": "itom-xservices"
                },
                {
                    "name": "itom-xservices-infra"
                },
                {
                    "name": "itom-service-portal"
                },
                {
                    "name": "itom-service-portal-ui"
                },
                {
                    "name": "itom-smartanalytics"
                }
            ]
        }
    ]
}

let mockDataGetBackupStatus1 ={
                     'backupStatus': 'BACKUPING',
                     'backupProcess': 'COMPRESS_BACKUP_PACKAGE',
                     'backupErrorCode': '0',
                     'backupPackageName': 'ITSMA_v2017.07_1504064996638.zip',
                     'backupStartTime': 'Web Aug 30 14:49:52 CST 2017'
                };

let mockDataStartBackup ='BACKUP PROCESS CREATED';

let mockBackupService= [
        {
            'key': 'itom_suite_mode',
            'value': 'H_Mode'
        },
        {
            'key': 'itom-xservices',
            'value': true
        },
        {
            'key': 'itom_service_sm',
            'value': false
        },
        {
            'key': 'itom_service_chat',
            'value': true
        },
        {
            'key': 'itom_service_smartanalytics',
            'value': true
        },
        {
            'key': 'itom_service_ucmdb',
            'value': false
        },
        {
            'key': 'itom-xruntime-infra',
            'value': false
        },
        {
            'key': 'itom-service-portal-ui',
            'value': true
        },
        {
            'key': 'itom-xruntime',
            'value': false
        },
        {
            'key': 'itom_service_propel',
            'value': true
        },
        {
            'key': 'itom-xservices-infra',
            'value': true
        },
        {
            'key': 'external_db',
            'value': false
        }
    ];



module.exports = function (app) {
  let startBackup = true;  //check if backup was already started
  let count = 0;
  
  

  app.use('/api/config/backupservice',function(req,res){
        let activeServices = [];
        for(let service in mockBackupService){
            if(mockBackupService[service].key == 'external_db' || mockBackupService[service].key == 'itom_suite_mode'){
                continue;
            }else{
                if(mockBackupService[service].value){
                    activeServices.push(mockBackupService[service].key);
                }
            }
        }
        res.json(activeServices);
  });

  //get backup status
  app.use('/suitebackup/backup/status',function(req,res) {
    let mockData = mockDataGetBackupStatus1;

    if(startBackup){
        mockData.backupStatus = null;
        mockData.backupErrorCode = null;
        mockData.backupProcess = null;
        mockData.backupPackageName = null;
        mockData.backupStartTime = null;
        res.json(mockData);
        return;
    }

    if(count >13){
        mockData.backupStatus = 'SUCCESS';
        mockData.backupErrorCode = '0';
        mockData.backupProcess = 'REMOVE_COMPRESSED_PACKAGE';
        mockData.backupPackageName = 'ITSMA_v2017.07_1504064996638.zip';
        mockData.backupStartTime = 'Web Aug 30 14:49:52 CST 2017';
        count = 0;
        startBackup = true;
    }else if(count <=13){
        mockData.backupStatus = 'BACKUPING';
        mockData.backupErrorCode = '0';
        mockData.backupProcess = backupProcess[count];
        mockData.backupPackageName = 'ITSMA_v2017.07_1504064996638.zip';
        mockData.backupStartTime = 'Web Aug 30 14:49:52 CST 2017';
        count++;
    }
    res.json(mockData);
  });

  //get a list of backup zip package with metadata
  app.use('/suitebackup/backup/packagelist',function(req,res) {
    let mockData = mockDataGetFiles;
    res.json(mockData);
    
  });
    
  //start backup
  app.use('/suitebackup/backup', function (req, res) {
    let mockData = mockDataStartBackup;
    if(startBackup){
            mockData = 'BACKUP PROCESS CREATED';
            startBackup = false;
    }else{
        mockData = 'ALREADY HAVE BACKUP PROCESS, PLEASE WAIT';
    }
    
    res.json(mockData);
  });

};
