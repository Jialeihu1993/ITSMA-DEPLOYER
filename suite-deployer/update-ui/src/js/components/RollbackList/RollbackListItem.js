import React from 'react'
import {Collapse,Well, ProgressBar} from 'react-bootstrap';
import './RollbackList.scss';

class RollbackListItem extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
        errorMsgOpen:false
    };
  }

  //status: finished error success pending
  statusStyleIcon(){
      /*
      switch (this.props.status){
        case 'finished':
            return '';
        case 'success':
            return 'ion ion-checkmark-circled';
        case 'pending':
            return 'ion ion-load-c fa-spin indicator-pending'
        case 'error':
            return 'ion indicator-error ion-close-circled'
        default:
            return ''
      }
      */
      if(this.props.percent){
          if(this.props.percent >= 100){
              return 'ion ion-checkmark-circled restore-success';
          }
      }
  }
 //TERMINATED  READY  SUCCESS
  statusStyle(){
    switch (this.props.status){
        case 'finished':
            return 'indicator-finished';
        case 'success':
            return 'indicator-finished';
        case 'pending':
            return 'content-indicator-pending';
        case 'error':
            return 'indicator-error';
        default:
            return 'indicator-finished'
  }
}


  errorShow(){
    
      if(this.props.errorMsg){
          let ifOpen = this.state.errorMsgOpen;
          this.setState({errorMsgOpen:!ifOpen});
      }
      
      //console.log('clicked: '+this.props.key);
      let nextStatus;
      if(this.props.status == 'focus'){
            nextStatus = 'finished';
      }else{
        nextStatus = 'focus'
      }
      if(this.props.showStatus){
          this.props.showStatus(nextStatus);
      }
  }

  ifErrorShow(){
      /*
      if(this.state.errorMsgOpen){
          return ({paddingBottom:'0px'});
      }else{
          return({paddingBottom:'10px'});
      }
      */
      if(this.props.status == 'focus'){
          return ({backgroundColor:'#f0f0f0'});
      }else{
          return ({backgroundColor:'#ffffff'});
      }
  }

  restore(){
      if(this.props.startRestore){
        return(
                <ProgressBar bsStyle="success" now={this.props.percent} />
        );
      }else{
          return;
      }
  }

 
  render(){
      return(
            <li  className="list-group-item" style={this.ifErrorShow()} onClick={() =>this.errorShow()} >
                
                <span className={`${'content '} ${this.statusStyle()}`}>{this.props.name}</span>
                <span className={`${'timeTag '} ${this.statusStyle()}`}>{this.props.timeTag}</span>
                <span className={`${'indicator  '} ${this.statusStyleIcon()}`}></span>

                <Collapse in={this.state.errorMsgOpen}>
                    <div>
                    <div className='arrow-up'></div>
                        <Well>
                            {this.props.errorMsg}
                            <span>More Detail</span>
                        </Well>
                    </div>
                </Collapse>
                {this.restore()}
            </li>

      );
  }


}

RollbackListItem.propTypes = {
    name: React.PropTypes.string,
    timeTag: React.PropTypes.string,
    status: React.PropTypes.string,
    errorMsg: React.PropTypes.string,
    showStatus:React.PropTypes.func,
    startRestore:React.PropTypes.bool,
    percent:React.PropTypes.number
};


export default RollbackListItem;