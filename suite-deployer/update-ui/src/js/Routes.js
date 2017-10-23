import React from 'react';
import { Route, IndexRoute, Router, Redirect, browserHistory} from 'react-router';
import { Provider } from 'react-redux';
import configureStore from './stores/configureStore';
import Main from './Main';
import VersionInfo from './pages/VersionInfo';
import UpdateRollBack from './pages/UpdateRollBackContainer';
import Update from './pages/UpdateContainer';
import BackupFiles from './pages/BackupFilesContainer';
import Shutdown from './pages/ShutdownContainer';
import BackupDataBase from './pages/BackupDataBase';
import StartServices from './pages/StartServicesContainer';
import Summary from './pages/Summary';
import {BASE_NAME} from 'constants/ServiceConfig';
import upgrade from './pages/upgradeRollbackContainer';
/**
 * Routes: https://github.com/reactjs/react-router/blob/master/docs/API.md#route
 *
 * Routes are used to declare your view hierarchy.
 *
 * Say you go to http://material-ui.com/#/components/paper
 * The react router will search for a route named 'paper' and will recursively render its
 * handler and its parent handler like so: Paper > Components > Master
 */
let store = configureStore();

class Routes extends React.Component {
  render() {
    return (
        <div>
          <Provider store={store}>
            <Router history={browserHistory}>
              <Route path={BASE_NAME} component={Main}>
                <IndexRoute component={BackupFiles} />
                <Route path='info' component={VersionInfo}/>
                <Route path='shutdown' component={Shutdown}/>
                <Route path='backup' component={BackupFiles}/>
                <Route path='upgrade' component={upgrade}/>
                <Route path='rollback' component={UpdateRollBack}/>
                <Route path='database' component={BackupDataBase}/>
                <Route path='service' component={StartServices}/>
                <Route path='summary' component={Summary}/>
                <Redirect from='/' to={BASE_NAME} />
                <Route path='*' component={BackupFiles}/>
              </Route>
            </Router>
          </Provider>
        </div>
    )
  }
}

export default Routes;
