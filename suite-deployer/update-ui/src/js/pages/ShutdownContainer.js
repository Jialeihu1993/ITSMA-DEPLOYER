import { connect } from 'react-redux';

import Shutdown from './Shutdown';

let select = (state) => {
    return {
      servicestatus: state.shutdownReducers.servicestatus,
      isLoaded: state.shutdownReducers.isLoaded,
      phaseName: state.shutdownReducers.phaseName,
      phaseDetail: state.shutdownReducers.phaseDetail,
      startInfo: state.shutdownReducers.startInfo
    };
  }
  

export default connect(select)(Shutdown);