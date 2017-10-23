import React from 'react'
import {Collapse,Well} from 'react-bootstrap';
import './CommonList.scss';

class CommonListItem extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
        errorMsgOpen:false
    };
  }

  //status: initial pending finish error
  statusStyleIcon(status){
      switch (status){
        case 'RUNNING':
                return 'ion ion-checkmark-circled';
        case 'SUCCESS':
            return 'ion ion-checkmark-circled';
        case 'TERMINATED':
            return 'ion ion-minus-circled';
        case 'SHUTTING_DOWN_ERROR':
            return 'ion indicator-error ion-close-circled';
        case 'TERMINATING':
            return 'ion ion-load-c fa-spin indicator-pending';
        case 'READY':
            return 'ion ion-load-c fa-spin indicator-pending';
        default:
            return 'ion ion-checkmark-circled';
      }
  }

  statusStyle(status){
    switch (status){
      case 'SHUTTING_DOWN_ERROR':
          return 'indicator-error';
      default:
          return '';
    }
}

  errorShow(){
      if(this.props.errorMsg && this.props.status == 'SHUTTING_DOWN_ERROR'){
          let ifOpen = this.state.errorMsgOpen;
          this.setState({errorMsgOpen:!ifOpen});
      }
  }

  ifErrorShow(){
      if(this.state.errorMsgOpen){
          return ({paddingBottom:'0px'});
      }else{
          return({paddingBottom:'10px'});
      }
  }

  render(){
      return(
            <li  className="list-group-item" style={this.ifErrorShow()} onClick={() =>this.errorShow()}>
                
                <span className={`${'indicator '} ${this.statusStyleIcon(this.props.status)}`}></span>
                <span className={`${'content '} ${this.statusStyle(this.props.status)} `}>{this.props.name}</span>

                <Collapse in={this.state.errorMsgOpen}>
                    <div>
                    <div className='arrow-up'></div>
                        <Well>
                            {this.props.errorMsg}
                            <span>More Detail</span>
                        </Well>
                    </div>
                </Collapse>
            </li>

      );
  }


}

CommonListItem.propTypes = {
    type: React.PropTypes.string,
    name: React.PropTypes.string,
    status: React.PropTypes.string,
    errorMsg: React.PropTypes.string,
    reverse: React.PropTypes.bool
};


export default CommonListItem;