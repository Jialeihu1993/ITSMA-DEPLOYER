import {polyfill} from 'es6-promise';
import axios from 'axios';
import {UPGRADE_LOAD_DATA} from '../constants/ActionTypes';
import {UPGRADE_LOAD_DATA_URL} from '../constants/ServiceConfig';

// fix axios can't work issue in IE(https://github.com/stefanpenner/es6-promise)
polyfill();

export function loadData(isLoaded,action) {
  return function (dispatch) {
    if (!isLoaded) {
      let time = new Date().getTime();
      axios.get(UPGRADE_LOAD_DATA_URL + '/' + time + '/' + action)
          .then(function (response) {
            dispatch(initDataSuccess(response.data));
          })
          .catch(function () {
          });
    }
  }
}

export function initDataSuccess(value) {
    return {
      type: UPGRADE_LOAD_DATA,
      value: value
    }
  }

  
