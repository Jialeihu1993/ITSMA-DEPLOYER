import React from 'react'
import {ListGroup} from 'react-bootstrap';
import RollbackListItem from './RollbackListItem'
import {FormattedMessage} from 'react-intl';
import './RollbackList.scss';
import clone from 'lodash/clone';

class RollbackList extends React.Component {

  constructor(props) {
    super(props);
  }

  componentWillReceiveProps(nextProps){
    this.setState({datasLocal:nextProps.datas});
  }
//item:{name:'',timeTag:'',status:''}
  datasRefresh(lsStatus,currentIndex){
    //console.log(lsStatus+' :  '+currentIndex);
    //check other liStatus and their index
    //let datasCopy = clone(this.state.datasLocal);
        if(!this.props.startRestore){
          /*
            let currentDatas = this.state.datasLocal;
            currentDatas.map((item,index) => {
                if(currentDatas[index].status == 'focus' && index != currentIndex){
                  currentDatas[index].status = 'finished';
                }
            });
            currentDatas[currentIndex].status = lsStatus;
         */
          if(this.props.refreshList){
            this.props.refreshList(lsStatus,currentIndex);
          }

          if(this.props.canRetry){
            if(lsStatus == 'focus'){
              this.props.canRetry(true);
            }else{
              this.props.canRetry(false);
            }
          }
      }
  }


  render(){
    let datas = this.props.datas.map((item,index) =>{
      if(item.status == 'focus'){
          return(
            <RollbackListItem  key={index}  name={item.name} timeTag={item.timeTag} status={item.status} errorMsg={item.errorMsg} showStatus={(liStatus)=>{this.datasRefresh(liStatus,index)}} startRestore={this.props.startRestore} percent={this.props.percent} />
          );
      }else{
          return(
            <RollbackListItem  key={index}  name={item.name} timeTag={item.timeTag} status={item.status} errorMsg={item.errorMsg} showStatus={(liStatus)=>{this.datasRefresh(liStatus,index)}} startRestore={false} />
          );
      }
    });
     
      return(
         <ListGroup componentClass='ul' className='RollbackList'>
            <li key='title' className="list-group-item titleItem" >
                <span className='title' style={{display:'inline-block',marginLeft:'30px'}}><FormattedMessage id='Name' /></span>
                <span className='title' style={{display:'inline-block',position:'absolute', left:'50%'}}><FormattedMessage id='Time' /></span>
            </li>
           {datas}
        </ListGroup>

      );
  }


}

RollbackList.propTypes = {
    //name time status
    datas: React.PropTypes.array,
    canRetry: React.PropTypes.func,
    refreshList: React.PropTypes.func,
    startRestore: React.PropTypes.bool,
    percent:React.PropTypes.number
};


export default RollbackList;