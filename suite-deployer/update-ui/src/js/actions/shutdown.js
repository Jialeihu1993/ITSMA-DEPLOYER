import {polyfill} from 'es6-promise';
import axios from 'axios';
import {SHUTDOWN_START,SHUTDOWN_STATUS,SHUTDOWN_FINISHED,SHUTDOWN_RUNNING} from '../constants/ActionTypes';
import {SHUTDOWN_START_URL,SHUTDOWN_STATUS_URL} from '../constants/ServiceConfig';

// fix axios can't work issue in IE(https://github.com/stefanpenner/es6-promise)
polyfill();

export function startShutdown() {
  return function (dispatch) {
      axios.post(SHUTDOWN_START_URL)
          .then(function () {
            dispatch(shutdownStart());
          })
          .catch(function () {
          });
  }
}

export function statusShutdown() {
  return function (dispatch) {
      axios.get(SHUTDOWN_STATUS_URL)
          .then(function (response) {
            dispatch(shutdownStatus(response.data));
          })
          .catch(function () {
          });
  }
}


export function shutdownStart() {
    return {
      type: SHUTDOWN_START
    }
  }

export function shutdownRunning() {
    return {
      type: SHUTDOWN_RUNNING
    }
  }

export function shutdownFinished() {
    return {
      type: SHUTDOWN_FINISHED
    }
  }

export function shutdownStatus(value) {
    return {
      type: SHUTDOWN_STATUS,
      value: value
    }
}




  
