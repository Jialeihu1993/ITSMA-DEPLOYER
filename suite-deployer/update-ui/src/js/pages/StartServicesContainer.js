import { connect } from 'react-redux';

import StartServices from './StartServices';

let select = (state) => {
    return {
      servicestatus: state.serviceReducers.servicestatus,
      isLoaded: state.serviceReducers.isLoaded,
      phaseName: state.serviceReducers.phaseName,
      phaseDetail: state.serviceReducers.phaseDetail,
      startInfo: state.serviceReducers.startInfo
    };
  }
  

export default connect(select)(StartServices);