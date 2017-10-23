import {SHUTDOWN_START,SHUTDOWN_STATUS,SHUTDOWN_FINISHED,SHUTDOWN_RUNNING} from '../constants/ActionTypes';
//import {_keys} from 'lodash/keys';
import { cloneDeep } from 'lodash';

const initialState = {
   'isLoaded':false,
   'phaseName':'',
   'phaseDetail':'',
   'servicestatus':[],
   'startInfo':''
};


const handlers = {
    [SHUTDOWN_START]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'SHUTDOWN STARTED';
      return newState;
    },
    [SHUTDOWN_STATUS]: (state, action) => {
      let newState = Object.assign({},state);
      newState.phaseName = action.value.phase.name;
      newState.phaseDetail = action.value.phase.detail;
      newState.servicestatus = action.value.itsmaServiceStatuses;
      return {...newState,isLoaded:true};
    },
    [SHUTDOWN_RUNNING]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'SHUTDOWN RUNNING';
      newState.phaseName = '';
      return newState;
    },
    [SHUTDOWN_FINISHED]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'SHUTDOWN FINISHED';
      return newState;
    }
};

export default function shutdownReducers(state = initialState, action) {
    let handler = handlers[action.type];
    if (!handler) return cloneDeep(state);
    return { ...state, ...handler(state, action) };
  }