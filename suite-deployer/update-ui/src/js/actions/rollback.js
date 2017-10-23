import {polyfill} from 'es6-promise';
import axios from 'axios';
import {ROLLBACK_START,ROLLBACK_STATUS,ROLLBACK_RUNNING,ROLLBACK_FINISHED} from '../constants/ActionTypes';
import {ROLLBACK_START_URL,ROLLBACK_STATUS_URL} from '../constants/ServiceConfig';

// fix axios can't work issue in IE(https://github.com/stefanpenner/es6-promise)
polyfill();

export function startRollback() {
  return function (dispatch) {
      axios.post(ROLLBACK_START_URL)
          .then(function () {
            dispatch(rollbackStart());
          })
          .catch(function () {
          });
  }
}

export function statusRollback() {
  return function (dispatch) {
      axios.get(ROLLBACK_STATUS_URL)
          .then(function (response) {
            dispatch(rollbackStatus(response.data));
          })
          .catch(function () {
          });
  }
}


export function rollbackStart() {
    return {
      type: ROLLBACK_START
    }
  }

export function rollbackRunning() {
    return {
      type: ROLLBACK_RUNNING
    }
  }

export function rollbackFinished() {
    return {
      type: ROLLBACK_FINISHED
    }
  }

export function rollbackStatus(value) {
    return {
      type: ROLLBACK_STATUS,
      value: value
    }
}




  


  

