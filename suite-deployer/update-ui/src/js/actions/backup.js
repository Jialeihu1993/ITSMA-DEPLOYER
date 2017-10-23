import {polyfill} from 'es6-promise';
import axios from 'axios';
import {BACKUP_START, BACKUP_STATUS,BACKUP_FILES,BACKUP_SERVICES,BACKUP_CLEARBACKUPINIT} from '../constants/ActionTypes';
import {BACKUP_START_URL,BACKUP_STATUS_URL,BACKUP_FILES_URL,BACKUP_SERVICES_URL} from '../constants/ServiceConfig';

// fix axios can't work issue in IE(https://github.com/stefanpenner/es6-promise)
polyfill();

export function startBackup() {
  return function (dispatch) {
      axios.post(BACKUP_START_URL)
          .then(function () {
            dispatch(backupStart());
          })
          .catch(function () {
          });
  }
}

export function statusBackup() {
  return function (dispatch) {
      axios.get(BACKUP_STATUS_URL)
          .then(function (response) {
            dispatch(backupStatus(response.data));
          })
          .catch(function () {
          });
  }
}

export function backupServices() {
  return function (dispatch) {
      axios.get(BACKUP_SERVICES_URL)
          .then(function (response) {
            dispatch(getServices(response.data));
          })
          .catch(function () {
          });
  }
}



export function getBackupFiles() {
  return function (dispatch) {
      axios.get(BACKUP_FILES_URL)
          .then(function (response) {
            dispatch(backupFiles(response.data));
          })
          .catch(function () {
          });
  }
}


export function backupStart(value) {
    return {
      type: BACKUP_START,
      value: value
    }
  }

export function backupStatus(value) {
    return {
      type: BACKUP_STATUS,
      value: value
    }
  }

export function backupFiles(value) {
    return {
      type: BACKUP_FILES,
      value: value
    }
  }

export function getServices(value) {
    return {
      type: BACKUP_SERVICES,
      value: value
    }
  }

export function clearBackupStatusInit() {
  return {
    type: BACKUP_CLEARBACKUPINIT
  }
}



  
