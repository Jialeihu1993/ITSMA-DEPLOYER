import { connect } from 'react-redux';

import Update from './Update';

let select = (state) => {
    return {
      lists: state.upgradeReducers.lists,
      isLoaded: state.upgradeReducers.isLoaded,
      complete: state.upgradeReducers.complete,
      upgradeStatus: state.upgradeReducers.upgradeStatus
    };
  }
  

export default connect(select)(Update);