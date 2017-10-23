import {UPDATE_START,UPDATE_STATUS,UPDATE_FINISHED,UPDATE_RUNNING} from '../constants/ActionTypes';
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
    [UPDATE_START]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'UPDATE STARTED';
      return newState;
    },
    [UPDATE_STATUS]: (state, action) => {
      let newState = Object.assign({},state);
      newState.phaseName = action.value.phase.name;
      newState.phaseDetail = action.value.phase.detail;
      newState.servicestatus = action.value.itsmaServiceStatuses;
      return {...newState,isLoaded:true};
    },
    [UPDATE_RUNNING]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'UPDATE RUNNING';
      newState.phaseName = '';
      return newState;
    },
    [UPDATE_FINISHED]: (state) => {
      let newState = Object.assign({}, state);
      newState.startInfo = 'UPDATE FINISHED';
      return newState;
    }
};

export default function serviceReducers(state = initialState, action) {
    let handler = handlers[action.type];
    if (!handler) return cloneDeep(state);
    return { ...state, ...handler(state, action) };
  }