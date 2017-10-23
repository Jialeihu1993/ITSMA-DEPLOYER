import { createStore,  applyMiddleware } from 'redux';
import thunkMiddleware from 'redux-thunk';
import combineReducers from './combineReducers';


export default function configureStore(initialState) {

  let store = createStore(combineReducers, initialState, applyMiddleware(thunkMiddleware));

  return store;
}
