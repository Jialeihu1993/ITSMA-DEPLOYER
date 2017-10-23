import React from 'react'
import {Collapse,Well,ProgressBar} from 'react-bootstrap';
import './TableList.scss';

class TableListItem extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
        errorMsgOpen:false
    };
  }

  //status: finished error success pending
  statusStyleIcon(status){
      switch (status){
        case 'finish':
            return '';
        case 'success':
            return 'ion ion-checkmark-circled indicator-success';
        case 'pending':
            return 'ion ion-load-c fa-spin indicator-pending'
        case 'error':
            return 'ion indicator-error ion-close-circled'
        default:
            return ''
      }
  }

  statusStyle(status){
    switch (status){
      case 'finished':
          return 'indicator-finished';
      case 'success':
          return 'content-indicator-pending';
      case 'pending':
          return 'content-indicator-pending';
      case 'error':
          return 'indicator-error';
      default:
          return ''
    }
}

  errorShow(){
      if(this.props.errorMsg){
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

  startBackup(){
    if(this.props.startBackup){
      return(
              <ProgressBar bsStyle="success" now={this.props.percent} />
      );
    }else{
        return;
    }
}

  render(){
      return(
            <li  className="list-group-item" style={this.ifErrorShow()} onClick={() =>this.errorShow()}>
                
                <span className={`${'content '} ${this.statusStyle(this.props.status)}`}>{this.props.name}</span>
                <span className={`${'timeTag '} ${this.statusStyle(this.props.status)}`}>{this.props.timeTag}</span>
                <span className={`${'indicator  '} ${this.statusStyleIcon(this.props.status)}`}></span>

                <Collapse in={this.state.errorMsgOpen}>
                    <div>
                    <div className='arrow-up'></div>
                        <Well>
                            {this.props.errorMsg}
                            <span>More Detail</span>
                        </Well>
                    </div>
                </Collapse>

                {this.startBackup()}

            </li>
      );
  }


}

TableListItem.propTypes = {
    name: React.PropTypes.string,
    timeTag: React.PropTypes.string,
    status: React.PropTypes.string,
    errorMsg: React.PropTypes.string,
    startBackup:React.PropTypes.bool,
    percent:React.PropTypes.number
};


export default TableListItem;