import {UPGRADE_LOAD_DATA} from '../constants/ActionTypes';
import { cloneDeep } from 'lodash';
const initialState = {
   isLoaded:false,
   lists: [
    {type:'Configmap', per:0,status:true},//status false with error true success
    {type:'Vault', per:0,status:true},    //per 0 - 100
    {type:'Ingress', per:0,status:true},
    {type:'Others', per:0,status:true}
  ],
  complete:false,
  upgradeStatus:false
};


const handlers = {
    [UPGRADE_LOAD_DATA]: (state, action) => {
      let newState = Object.assign({}, action.value);
      return { ...newState, isLoaded:true};
    }
};

export default function upgradeReducers(state = initialState, action) {
    let handler = handlers[action.type];
    if (!handler) return cloneDeep(state);
    return { ...state, ...handler(state, action) };
  }