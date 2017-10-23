import {ROLLBACK_FINISHED,ROLLBACK_START,ROLLBACK_RUNNING,ROLLBACK_STATUS} from '../constants/ActionTypes';
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
    [ROLLBACK_START]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'ROLLBACK STARTED';
      return newState;
    },
    [ROLLBACK_STATUS]: (state, action) => {
      let newState = Object.assign({},state);
      newState.phaseName = action.value.phase.name;
      newState.phaseDetail = action.value.phase.detail;
      newState.servicestatus = action.value.itsmaServiceStatuses;
      return {...newState,isLoaded:true};
    },
    [ROLLBACK_RUNNING]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'ROLLBACK RUNNING';
      newState.phaseName = '';
      return newState;
    },
    [ROLLBACK_FINISHED]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'ROLLBACK FINISHED';
      return newState;
    }
    
};

export default function rollbackReducers(state = initialState, action) {
    let handler = handlers[action.type];
    if (!handler) return cloneDeep(state);
    return { ...state, ...handler(state, action) };
  }