import { createStore, applyMiddleware, compose } from 'redux';
import thunkMiddleware from 'redux-thunk';
import createLogger from 'redux-logger';
import combineReducers from './combineReducers';


const loggerMiddleware = createLogger();

export default function configureStore(initialState) {
  let store;
  if (window.devToolsExtension) { //Enable Redux devtools if the extension is installed in developer's browser
    store = createStore(

      combineReducers,
      initialState,
      compose(
        applyMiddleware(thunkMiddleware),
        window.devToolsExtension()
      )
    );
  } else {
    store = createStore(
      combineReducers,
      initialState,
      applyMiddleware(
        thunkMiddleware,
        loggerMiddleware
      )
    );
  }

  if (module.hot) {
    module.hot.accept('../reducers/shutdown', () => {
      const nextReducer = require('../reducers/shutdown');
      store.replaceReducer(nextReducer);
    });
    module.hot.accept('../reducers/backup', () => {
      const nextReducer = require('../reducers/backup');
      store.replaceReducer(nextReducer);
    });
    
  }
  return store;
  
}
