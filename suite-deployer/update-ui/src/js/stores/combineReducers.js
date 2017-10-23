// configure reducers for both productino and dev enviornment
import {combineReducers} from 'redux';
import shutdownReducers from '../reducers/shutdown';
import backupReducers from '../reducers/backup';
import upgradeReducers from '../reducers/upgrade';
import serviceReducers from '../reducers/service';
import rollbackReducers from '../reducers/rollback';
const Reducers = {
    shutdownReducers,
    backupReducers,
    upgradeReducers,
    serviceReducers,
    rollbackReducers
};

export default combineReducers(Reducers);
