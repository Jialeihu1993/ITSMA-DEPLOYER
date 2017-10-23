import { connect } from 'react-redux';

import UpdateRollBack from './UpdateRollBack';

let select = (state) => {
  return {
    servicestatus: state.rollbackReducers.servicestatus,
    isLoaded: state.rollbackReducers.isLoaded,
    phaseName: state.rollbackReducers.phaseName,
    phaseDetail: state.rollbackReducers.phaseDetail,
    startInfo: state.rollbackReducers.startInfo,
    backup_list: state.backupReducers.backup_list
  };
}
  

export default connect(select)(UpdateRollBack);