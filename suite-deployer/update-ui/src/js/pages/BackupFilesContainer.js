import { connect } from 'react-redux';

import BackupFiles from './BackupFiles';

let select = (state) => {
    return {
      backup_list: state.backupReducers.backup_list,
      isLoaded: state.backupReducers.isLoaded,
      backupStatus: state.backupReducers.backupStatus,
      backupProcess: state.backupReducers.backupProcess,
      backupErrorCode: state.backupReducers.backupErrorCode,
      backupStatusInit: state.backupReducers.backupStatusInit,
      backupPackageName: state.backupReducers.backupPackageName,
      backupStartTime: state.backupReducers.backupStartTime,
      backupServices: state.backupReducers.backupServices,
      backupNamespace:state.backupReducers.backupNamespace
      
      
    };
  }
  

export default connect(select)(BackupFiles);