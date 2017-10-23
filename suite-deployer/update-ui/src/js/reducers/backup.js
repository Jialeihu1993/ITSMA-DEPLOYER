import {BACKUP_START,BACKUP_STATUS,BACKUP_FILES,BACKUP_SERVICES,BACKUP_CLEARBACKUPINIT} from '../constants/ActionTypes';
import { cloneDeep } from 'lodash';
const initialState = {
   isLoaded:false,
   backup_list:[],
   backupStatus: '',   //this is the status of whole backup
   backupProcess: '',  // backup return failed when one backup procedure failed and would not continue other backup
   backupErrorCode: '',
   backupStatusInit: '',
   backupPackageName: '',
   backupStartTime: '',
   backupServices:[]
};


const handlers = {
    [BACKUP_START]: (state) => {
      let newState = Object.assign({}, state);
      newState.backupStatusInit = 'BACKUP PROCESS CREATED';
      return { ...newState, isLoaded:true};
    },
    [BACKUP_STATUS]: (state, action) => {
      let newState = Object.assign({}, state);
      newState.backupStatus = action.value.backupStatus;
      newState.backupProcess = action.value.backupProcess;
      newState.backupErrorCode = action.value.backupErrorCode;
      newState.backupPackageName = action.value.backupPackageName;
      newState.backupStartTime = action.value.backupStartTime;
      return { ...newState, isLoaded:true};
    },
    [BACKUP_FILES]: (state, action) => {
      let newState = Object.assign({}, state);
      newState.backup_list = action.value.backup_list;
      return { ...newState, isLoaded:true};
    },
    [BACKUP_SERVICES]: (state, action) => {
      let newState = Object.assign({}, state);
      newState.backupServices = action.value;
      return {...newState,isLoaded:true};
    },
    [BACKUP_CLEARBACKUPINIT]: (state) => {
      let newState = Object.assign({}, state);
      newState.backupStatusInit = '';
      newState.backupStatus = '';
      return newState;
   
    }
};

export default function backupReducers(state = initialState, action) {
    let handler = handlers[action.type];
    if (!handler) return cloneDeep(state);
    return { ...state, ...handler(state, action) };
  }