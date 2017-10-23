import {polyfill} from 'es6-promise';
import axios from 'axios';
import {UPDATE_START,UPDATE_STATUS,UPDATE_RUNNING,UPDATE_FINISHED} from '../constants/ActionTypes';
import {UPDATE_START_URL,UPDATE_STATUS_URL} from '../constants/ServiceConfig';

// fix axios can't work issue in IE(https://github.com/stefanpenner/es6-promise)
polyfill();

export function startUpdate() {
  return function (dispatch) {
      axios.post(UPDATE_START_URL)
          .then(function () {
            dispatch(updateStart());
          })
          .catch(function () {
          });
  }
}

export function statusUpdate() {
  return function (dispatch) {
      axios.get(UPDATE_STATUS_URL)
          .then(function (response) {
            dispatch(updateStatus(response.data));
          })
          .catch(function () {
          });
  }
}


export function updateStart() {
    return {
      type: UPDATE_START
    }
  }

export function updateRunning() {
    return {
      type: UPDATE_RUNNING
    }
  }

export function updateFinished() {
    return {
      type: UPDATE_FINISHED
    }
  }

export function updateStatus(value) {
    return {
      type: UPDATE_STATUS,
      value: value
    }
}




  


  
